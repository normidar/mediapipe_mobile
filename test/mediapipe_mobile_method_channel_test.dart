import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mediapipe_mobile/mediapipe_mobile_method_channel.dart';

void main() {
  MethodChannelMediapipeMobile platform = MethodChannelMediapipeMobile();
  const MethodChannel channel = MethodChannel('mediapipe_mobile');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
