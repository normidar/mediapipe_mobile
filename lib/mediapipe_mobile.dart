
import 'mediapipe_mobile_platform_interface.dart';

class MediapipeMobile {
  Future<String?> getPlatformVersion() {
    return MediapipeMobilePlatform.instance.getPlatformVersion();
  }
}
