import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:mediapipe_mobile/mediapipe_mobile_platform_interface.dart';

import 'face.dart';

class FaceDetectionUtil {
  /// detect face by image file
  /// if isConvertPositions it will convert the position to the capture
  static Future<List<Face>> detectFaceWithImage(String imagePath,
      {int? modelSelection,
      double? minDetectionConfidence,
      bool isFullSizePoint = true,
      bool isConvertPositions = true}) async {
    final result = await MediapipeMobilePlatform.instance
        .detectionFaceWithImage(imagePath,
            modelSelection: modelSelection,
            minDetectionConfidence: minDetectionConfidence);
    Int32List imageSize = result?['imageSize'];
    int width = imageSize[0];
    int height = imageSize[1];
    print(width);
    print(height);
    List detectionResult = result?['detectionResult'];
    return detectionResult.map((element) {
          Map e = element;
          final bb = e['boundingBox'];
          print(bb);
          Rect? rect;
          if (isConvertPositions) {
            rect = Rect.fromLTWH((1.0 - bb[1]) * width, (1.0 - bb[0]) * height,
                bb[2] * width, bb[3] * height);
          } else {
            rect = Rect.fromLTWH(bb[0], bb[1], bb[2], bb[3]);
          }
          return Face(rect);
        }).toList() ??
        <Face>[];
  }
}
