import 'package:flutter/material.dart';
import 'package:mediapipe_mobile/mediapipe_mobile_platform_interface.dart';

import 'face.dart';

class FaceDetectionUtil {
  /// detect face by image file
  static Future<List<Face>> detectFaceWithImage(String imagePath,
      {int? modelSelection, double? minDetectionConfidence}) async {
    final result =
        await MediapipeMobilePlatform.instance.detectionFaceWithImage(
      imagePath,
      modelSelection: modelSelection,
      minDetectionConfidence: minDetectionConfidence,
    );
    print(result);
    return result?.map((e) {
          final bb = e['boundingBox'];
          return Face(Rect.fromLTWH(bb[0], bb[1], bb[2], bb[3]));
        }).toList() ??
        [];
  }
}
