# ML Notebook Guide

Notebook file: `DiabeticFootDetection.ipynb`

## Scope

The notebook includes:
- MobileNetV2-based training for binary classification
- Evaluation (accuracy, confusion matrix, sensitivity/specificity, ROC-AUC)
- Keras-to-TFLite conversion
- Saliency/Grad-CAM-style visualization cells

## Environment

Install dependencies:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements-notebook.txt
```

## Dataset Inputs Expected

Source dataset used in the paper:
- Plantar Thermogram Database for diabetic foot complication study
- 167 subjects (122 diabetic, 45 control)

The notebook expects three CSV files:
- `train_data.csv`
- `val_data.csv`
- `test_data.csv`

Expected columns:
- `filename`
- `label`

Typical generator config in notebook:
- image size: `(224, 224)`
- batch size: `32`
- class mode: `categorical`

## Dataset Citation

- Daniel Hernandez-Contreras, Hayde Peregrina-Barreto, Jose Rangel-Magdaleno, and Francisco Renero-Carrillo, "Plantar thermogram database for the study of diabetic foot complications," 2019.

## Path Changes Required

Notebook cells reference Google Drive paths like:
- `/content/drive/MyDrive/NEW/...`
- `/content/path_to_save_model/...`

For local runs:
1. Set a local `base_path`
2. Update CSV and model save/load paths

## Produced Artifacts

From notebook workflow:
- Keras model (`.h5` / `.keras`)
- TFLite model (`.tflite`)

The Android app currently uses:
- `IgniSoleApplication/app/src/main/assets/dfu_mobilenetv2.tflite`
- `IgniSoleApplication/app/src/main/assets/labels.txt`
