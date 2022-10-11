import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'mediapipe_mobile_platform_interface.dart';

/// An implementation of [MediapipeMobilePlatform] that uses method channels.
class MethodChannelMediapipeMobile extends MediapipeMobilePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('mediapipe_mobile');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
      {'str': 'my_str'},
    );
    return version;
  }
}
