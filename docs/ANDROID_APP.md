# Android App Guide

## Module

- App path: `IgniSoleApplication/app`
- Package: `com.example.ignisole`
- Min SDK: `26`
- Target SDK: `34`

## App Function

The app performs on-device diabetic foot image classification using:
- TensorFlow Lite model: `dfu_mobilenetv2.tflite`
- Labels: `labels.txt` (`Normal`, `Diabetic`)

## Image Inputs Supported

### Gallery

- Source: Android gallery picker (`Intent.ACTION_PICK`)
- MIME filter: `image/*`
- Behavior:
  - User selects an image URI
  - App decodes bitmap from URI stream
  - Non-image MIME is rejected

### Camera

- Source: camera capture intent (`MediaStore.ACTION_IMAGE_CAPTURE`)
- Behavior:
  - Captures an in-memory bitmap preview
  - Preview bitmap is used for inference

### Preprocessing

Before inference the bitmap is:
- resized to `224 x 224`
- converted to RGB float tensor
- normalized to `[-1, 1]` with `pixel / 127.5 - 1.0` (MobileNetV2 preprocessing)

## Build Commands

```bash
cd IgniSoleApplication
JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
PATH="$JAVA_HOME/bin:$PATH" \
ANDROID_HOME="$HOME/Library/Android/Sdk" \
ANDROID_SDK_ROOT="$HOME/Library/Android/Sdk" \
GRADLE_USER_HOME="$PWD/.gradle-local" \
./gradlew :app:assembleDebug :app:assembleRelease
```

Outputs:
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK (unsigned): `app/build/outputs/apk/release/app-release-unsigned.apk`

## Signing Release APK (Optional)

If you need Play Store or production distribution, sign the release artifact with your keystore.
