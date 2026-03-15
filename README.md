# IgniSole: Diabetic Foot Ulcer Detection

IgniSole is a smartphone-based, offline edge system for early diabetic foot ulcer (DFU) detection from plantar thermal images. It uses a fine-tuned MobileNetV2 model compressed to TensorFlow Lite and deployed directly on an Android device — no internet connection or server required.

## Contents

| Path | Description |
|---|---|
| `DiabeticFootDetection.ipynb` | Model training, evaluation, and TFLite export |
| `IgniSoleApplication/` | Android application source |
| `releases/` | Pre-built APK artifacts |
| `docs/` | Detailed guides for the notebook and app |
| `requirements-notebook.txt` | Python dependencies for the notebook |

## Dataset

The model is trained on the **Plantar Thermogram Database** — an open-source dataset from IEEE Dataport for the study of diabetic foot complications.

- 167 subjects: 122 diabetic (DM group), 45 non-diabetic (control group)
- Left and right plantar thermal foot images
- Four plantar angiosome images with annotated temperature readings
- 80 / 10 / 10 train-validation-test split
- Input images resized to 224×224

**Citation:**
Daniel Hernandez-Contreras, Hayde Peregrina-Barreto, Jose Rangel-Magdaleno, and Francisco Renero-Carrillo, "Plantar thermogram database for the study of diabetic foot complications," 2019.

## Android Application

The app accepts a plantar thermal foot image (from gallery or camera) and runs on-device inference using the embedded TFLite model.

**Preprocessing pipeline:**
1. Resize to 224×224
2. Normalize to `[-1, 1]` via `pixel / 127.5 - 1.0` (MobileNetV2 standard)
3. Run TFLite interpreter → confidence scores for `Normal` / `Diabetic`

Supported input formats: `.jpg`, `.jpeg`, `.png`, `.webp`

See [`docs/ANDROID_APP.md`](docs/ANDROID_APP.md) for full details.

## Building the App

**Prerequisites:** Java 17+, Android SDK with `platforms;android-34` and `build-tools;34.0.0`

```bash
cd IgniSoleApplication
JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
PATH="$JAVA_HOME/bin:$PATH" \
ANDROID_HOME="$HOME/Library/Android/Sdk" \
ANDROID_SDK_ROOT="$HOME/Library/Android/Sdk" \
GRADLE_USER_HOME="$PWD/.gradle-local" \
./gradlew :app:assembleDebug :app:assembleRelease
```

Pre-built APKs are available in `releases/`:
- `IgniSole-debug.apk` — debug-signed, installable directly for testing
- `IgniSole-release-unsigned.apk` — unsigned release build

Verify integrity:
```bash
cd releases && LC_ALL=C LANG=C shasum -a 256 -c SHA256SUMS.txt
```

## Running the Notebook

The notebook was developed in Google Colab. Before running locally, update the dataset and model save/load paths (currently set to `/content/drive/...`).

**Expected CSV files:**

| File | Columns |
|---|---|
| `train_data.csv` | `filename`, `label` |
| `val_data.csv` | `filename`, `label` |
| `test_data.csv` | `filename`, `label` |

Install dependencies:
```bash
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements-notebook.txt
```

See [`docs/ML_NOTEBOOK.md`](docs/ML_NOTEBOOK.md) for full details.
