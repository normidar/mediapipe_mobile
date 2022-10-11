#import "MediapipeMobilePlugin.h"
#if __has_include(<mediapipe_mobile/mediapipe_mobile-Swift.h>)
#import <mediapipe_mobile/mediapipe_mobile-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mediapipe_mobile-Swift.h"
#endif

@implementation MediapipeMobilePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMediapipeMobilePlugin registerWithRegistrar:registrar];
}
@end
