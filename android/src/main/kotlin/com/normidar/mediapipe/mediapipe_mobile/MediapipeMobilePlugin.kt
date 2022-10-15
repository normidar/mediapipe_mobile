package com.normidar.mediapipe.mediapipe_mobile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.NonNull
import com.google.mediapipe.solutions.facedetection.FaceDetection
import com.google.mediapipe.solutions.facedetection.FaceDetectionOptions
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** MediapipeMobilePlugin */
class MediapipeMobilePlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "mediapipe_mobile")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }


  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        val str:String? = call.argument("str")
        result.success("Android ${android.os.Build.VERSION.RELEASE} $str")
      }
      "detectionFaceWithImage" -> {
        val modelSelection:Int? = call.argument("modelSelection")
        val minDetectionConfidence:Float? = call.argument("minDetectionConfidence")
        val faceDetectionOptionsBuilder = FaceDetectionOptions.builder()
        faceDetectionOptionsBuilder.setStaticImageMode(true)
        if (modelSelection != null) {
          faceDetectionOptionsBuilder.setModelSelection(modelSelection)
        }
        if (minDetectionConfidence != null) {
          faceDetectionOptionsBuilder.setMinDetectionConfidence(minDetectionConfidence)
        }
        val faceDetectionOptions = faceDetectionOptionsBuilder.build()
        val faceDetection = FaceDetection(context, faceDetectionOptions)
        faceDetection.setResultListener { faceDetectionResult ->
          val detections = faceDetectionResult.multiFaceDetections()
          // bitmap width height
          val bitmap = faceDetectionResult.inputBitmap()
          val width = bitmap.width
          val height = bitmap.height
          val imageSize = intArrayOf(width, height)
          // face detetion result
          val returnResult:MutableMap<String,Any> = mutableMapOf("imageSize" to imageSize)
          val detectionResult:MutableList<Map<String,Any>> = mutableListOf()
          for (e in detections) {
            val locationData = e.locationData
            if (!locationData.hasRelativeBoundingBox()) {
              continue
            }
            val boundingBox = locationData.relativeBoundingBox
            val boundingBoxArray: FloatArray = floatArrayOf(
              boundingBox.xmin, boundingBox.ymin,
              boundingBox.width,boundingBox.height
            )
            val keyPoints =locationData.relativeKeypointsList
            val keypointList = keyPoints.map {
              floatArrayOf(it.x,it.y)
            }
            detectionResult.add(
              mapOf("boundingBox" to boundingBoxArray, "keyPoints" to keypointList)
            )
          }
          returnResult["detectionResult"] = detectionResult
          result.success(returnResult)
          faceDetection.close()
        }
        faceDetection.setErrorListener { message, e ->
          result.error(e.hashCode().toString(),message,null)
          faceDetection.close()
        }
        // get the bitmap and send
        val imagePath:String? = call.argument("imagePath")
        if (imagePath != null) {
          try {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            faceDetection.send(bitmap)
          } catch (e:Exception) {
            result.error("image analyze fail", null, null)
          }
        } else {
          result.error("with out imagePath",null,null)
        }
      }
      "handsWithImage" -> {
        val maxNumHands:Int? = call.argument("maxNumHands")
        val isRunOnGpu:Boolean? = call.argument("isRunOnGpu")
        val minDetectionConfidence:Float? = call.argument("minDetectionConfidence")
        val minTrackingConfidence:Float? = call.argument("minTrackingConfidence")
        val modelComplexity:Int? = call.argument("modelComplexity")
        val isFullSizePoint:Boolean? = call.argument("isFullSizePoint")
        val handsOptionsBuilder = HandsOptions.builder()
        handsOptionsBuilder.setStaticImageMode(true)
        if (maxNumHands != null) {
          handsOptionsBuilder.setMaxNumHands(maxNumHands)
        }
        if (isRunOnGpu != null) {
          handsOptionsBuilder.setRunOnGpu(isRunOnGpu)
        }
        if (minDetectionConfidence != null) {
          handsOptionsBuilder.setMinDetectionConfidence(minDetectionConfidence)
        }
        if (minTrackingConfidence != null) {
          handsOptionsBuilder.setMinTrackingConfidence(minTrackingConfidence)
        }
        if (modelComplexity != null) {
          handsOptionsBuilder.setModelComplexity(modelComplexity)
        }
        val handsOptions = handsOptionsBuilder.build()
        val hands = Hands(context, handsOptions)
        hands.setResultListener { handsResult ->
          val detections = handsResult.multiHandLandmarks()
          // bitmap width height
          val bitmap = handsResult.inputBitmap()
          val width = bitmap.width
          val height = bitmap.height
          // face detetion result
          val detectionResult:MutableMap<String,Any> = mutableMapOf("imageSize" to arrayOf(width,height))
          val handsList = mutableListOf<Array<Array<Float>>>()
          for (e in detections) {
            val locationData = e.landmarkList
            val landmarkArray = locationData.map {
              return@map arrayOf(it.x, it.y, it.z)
            }.toTypedArray()
            handsList.add(landmarkArray)
          }
          detectionResult.put("detectionResult", handsList)
          result.success(detectionResult)
        }
        hands.setErrorListener { message, e ->
          result.error(e.hashCode().toString(),message,null)
        }
        // get the bitmap and send
        val imagePath:String? = call.argument("imagePath")
        if (imagePath != null) {
          try {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            hands.send(bitmap)
          } catch (e:Exception) {
            result.error("image analyze fail", null, null)
          }
        } else {
          result.error("with out imagePath",null,null)
        }
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
