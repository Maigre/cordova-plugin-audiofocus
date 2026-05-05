#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>

@interface AudioFocusPlugin : CDVPlugin
@property (nonatomic, copy) NSString* focusChangeCallbackId;
@end

@implementation AudioFocusPlugin

- (void)requestFocus:(CDVInvokedUrlCommand*)command {
    AVAudioSession* session = [AVAudioSession sharedInstance];
    NSError* error = nil;

    [session setCategory:AVAudioSessionCategoryPlayback error:&error];
    if (error) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR]
                                    callbackId:command.callbackId];
        return;
    }

    [session setActive:YES error:&error];
    if (error) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR]
                                    callbackId:command.callbackId];
        return;
    }

    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:AVAudioSessionInterruptionNotification
                                                  object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleInterruption:)
                                                 name:AVAudioSessionInterruptionNotification
                                               object:session];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""]
                                callbackId:command.callbackId];
}

- (void)cancelFocus:(CDVInvokedUrlCommand*)command {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:AVAudioSessionInterruptionNotification
                                                  object:nil];
    NSError* error = nil;
    [[AVAudioSession sharedInstance] setActive:NO
                                   withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
                                         error:&error];
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""]
                                callbackId:command.callbackId];
}

- (void)onFocusChange:(CDVInvokedUrlCommand*)command {
    self.focusChangeCallbackId = command.callbackId;
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
    result.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)handleInterruption:(NSNotification*)notification {
    if (self.focusChangeCallbackId == nil) return;

    NSDictionary* info = notification.userInfo;
    AVAudioSessionInterruptionType type =
        (AVAudioSessionInterruptionType)[info[AVAudioSessionInterruptionTypeKey] unsignedIntegerValue];

    NSString* focusState;
    if (type == AVAudioSessionInterruptionTypeBegan) {
        focusState = @"AUDIOFOCUS_LOSS";
    } else {
        // Interruption ended — reactivate the audio session so Howler can resume
        NSUInteger options = [info[AVAudioSessionInterruptionOptionKey] unsignedIntegerValue];
        if (options & AVAudioSessionInterruptionOptionShouldResume) {
            NSError* error = nil;
            [[AVAudioSession sharedInstance] setActive:YES error:&error];
        }
        focusState = @"AUDIOFOCUS_GAIN";
    }

    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:focusState];
    result.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:result callbackId:self.focusChangeCallbackId];
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
