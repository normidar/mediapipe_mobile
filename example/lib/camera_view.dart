import 'dart:io';

import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:mediapipe_mobile/mediapipe_solutions/face_detection/face_detection_util.dart';

class CameraView extends StatefulWidget {
  const CameraView({super.key, required this.camera});
  final CameraDescription camera;

  @override
  State<StatefulWidget> createState() => CameraViewState();
}

class CameraViewState extends State<CameraView> {
  late CameraController _controller;
  late Future<void> _initializeControllerFuture;
  double x = 0;
  bool isDetecting = false;

  @override
  void initState() {
    super.initState();
    _controller = CameraController(
      widget.camera,
      ResolutionPreset.medium,
    );
    _initializeControllerFuture = _controller.initialize();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: FutureBuilder<void>(
        future: _initializeControllerFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.done) {
            // If the Future is complete, display the preview.
            return CameraPreview(_controller);
          } else {
            // Otherwise, display a loading indicator.
            return const Center(child: CircularProgressIndicator());
          }
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          isDetecting = !isDetecting;
          if (isDetecting) {
            detectFace();
          }
        },
        child: Text(message),
      ),
    );
  }

  String message = '';
  Future detectFace() async {
    final picture = await _controller.takePicture();
    final result = await FaceDetectionUtil.detectFaceWithImage(picture.path,
        isFullSizePoint: true);
    if (result.isNotEmpty) {
      message = result.first.boundingBox.left.toString();
      setState(() {});
    }

    if (isDetecting) {
      detectFace();
    }
  }
}
