# IgniSole: Diabetic Foot Ulcer Detection

Description: IgniSole is a smartphone-based, offline edge system for early diabetic foot ulcer detection from plantar thermal images. The paper uses a fine-tuned MobileNetV2 model and deploys it as TensorFlow Lite in an Android app for on-device inference, lower internet dependency, and better data privacy. Reported results include 96.24% training accuracy (specificity 100%, sensitivity 92.85%) and 96.16% smartphone inference accuracy (specificity 98.88%, sensitivity 97.96%), with prediction time around 1-2 seconds.

## Project Overview

This repository contains:
- Research notebook for model training and evaluation: `DiabeticFootDetection.ipynb`
- Android application using TensorFlow Lite: `IgniSoleApplication/`
- Model assets bundled in the app:
  - `app/src/main/assets/dfu_mobilenetv2.tflite`
  - `app/src/main/assets/labels.txt`
- APK artifacts: `releases/`

## Dataset

The work uses the Plantar Thermogram Database (DFUC context in the paper) for diabetic foot analysis. As described in the paper, the dataset includes:
- 167 subjects in total
- 122 diabetic subjects (DM group)
- 45 non-diabetic subjects (control group)
- Left and right plantar thermal foot images
- Four plantar angiosome images with annotated temperature readings

The paper also reports an 80/10/10 train-validation-test split and 224x224 image resizing for model input.

## Dataset Citation

[1] Daniel Hernandez-Contreras, Hayde Peregrina-Barreto, Jose Rangel-Magdaleno, and Francisco Renero-Carrillo, "Plantar thermogram database for the study of diabetic foot complications," 2019.

## Repository Structure

```text
IgniSole/
├── DiabeticFootDetection.ipynb
├── IgniSoleApplication/
├── docs/
├── releases/
│   ├── IgniSole-debug.apk
│   ├── IgniSole-release-unsigned.apk
│   ├── README.md
│   └── SHA256SUMS.txt
├── scripts/
│   └── build_apks.sh
└── requirements-notebook.txt
```

## Project Status

- Notebook included for reproducible training and evaluation workflow
- Android application is buildable (API 34, AGP 8.4.1)
- APK artifacts generated and included
- Image input behavior documented (gallery and camera)

## APK Artifacts

- `releases/IgniSole-debug.apk`: signed debug build (installable for testing)
- `releases/IgniSole-release-unsigned.apk`: release build artifact (unsigned)

Verify integrity:

```bash
cd releases
LC_ALL=C LANG=C shasum -a 256 -c SHA256SUMS.txt
```

## Android Build Instructions

Prerequisites:
- Java 17+
- Android SDK with `platforms;android-34` and `build-tools;34.0.0`

Build commands:

```bash
cd IgniSoleApplication
JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
PATH="$JAVA_HOME/bin:$PATH" \
ANDROID_HOME="$HOME/Library/Android/Sdk" \
ANDROID_SDK_ROOT="$HOME/Library/Android/Sdk" \
GRADLE_USER_HOME="$PWD/.gradle-local" \
./gradlew :app:assembleDebug :app:assembleRelease
```

## Application Image Input Support

- Gallery picker accepts `image/*` MIME content
- Camera capture is supported via device camera intent
- Model input is resized to `224x224` RGB and normalized to `[0,1]`
- Labels are currently:
  - `Normal`
  - `Diabetic`

Recommended input files:
- `.jpg` / `.jpeg`
- `.png`
- `.webp`

Details: `docs/ANDROID_APP.md`

## Notebook and Dataset Notes

The notebook was originally developed in Google Colab with Drive paths (for example `/content/drive/...`). Update those paths before local runs.

CSV files expected by notebook:
- `train_data.csv`
- `val_data.csv`
- `test_data.csv`

Each CSV should contain:
- `filename` (path relative to dataset base path)
- `label` (class name)

Details: `docs/ML_NOTEBOOK.md`
