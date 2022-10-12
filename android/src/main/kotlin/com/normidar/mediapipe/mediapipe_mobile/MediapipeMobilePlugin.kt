package com.normidar.mediapipe.mediapipe_mobile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.NonNull
import com.google.mediapipe.solutions.facedetection.FaceDetection
import com.google.mediapipe.solutions.facedetection.FaceDetectionOptions

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
          if (detections.isEmpty()) {
            result.success(listOf(emptyMap<String, Any>()))
            return@setResultListener
          }
          // face detetion result
          val detectionResult:MutableList<Map<String,Any>> = mutableListOf()
          for (e in detections) {
            val locationData = e.locationData
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
          result.success(detectionResult)
        }
        faceDetection.setErrorListener { message, e ->
          result.error(e.hashCode().toString(),message,null)
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
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
