import 'package:flutter_test/flutter_test.dart';
import 'package:mediapipe_mobile/mediapipe_mobile.dart';
import 'package:mediapipe_mobile/mediapipe_mobile_platform_interface.dart';
import 'package:mediapipe_mobile/mediapipe_mobile_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMediapipeMobilePlatform
    with MockPlatformInterfaceMixin
    implements MediapipeMobilePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MediapipeMobilePlatform initialPlatform = MediapipeMobilePlatform.instance;

  test('$MethodChannelMediapipeMobile is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMediapipeMobile>());
  });

  test('getPlatformVersion', () async {
    MediapipeMobile mediapipeMobilePlugin = MediapipeMobile();
    MockMediapipeMobilePlatform fakePlatform = MockMediapipeMobilePlatform();
    MediapipeMobilePlatform.instance = fakePlatform;

    expect(await mediapipeMobilePlugin.getPlatformVersion(), '42');
  });
}
