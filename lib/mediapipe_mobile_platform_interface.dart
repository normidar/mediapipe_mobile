import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'mediapipe_mobile_method_channel.dart';

abstract class MediapipeMobilePlatform extends PlatformInterface {
  /// Constructs a MediapipeMobilePlatform.
  MediapipeMobilePlatform() : super(token: _token);

  static final Object _token = Object();

  static MediapipeMobilePlatform _instance = MethodChannelMediapipeMobile();

  /// The default instance of [MediapipeMobilePlatform] to use.
  ///
  /// Defaults to [MethodChannelMediapipeMobile].
  static MediapipeMobilePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MediapipeMobilePlatform] when
  /// they register themselves.
  static set instance(MediapipeMobilePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<Map?> detectionFaceWithImage(
    String imagePath, {int? modelSelection, double? minDetectionConfidence, bool? isFullSizePoint}
  ) {
    throw UnimplementedError('detectionFaceWithImage() has not been implemented.');
  }
}
