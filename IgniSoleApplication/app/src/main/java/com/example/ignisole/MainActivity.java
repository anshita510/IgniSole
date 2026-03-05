package com.example.ignisole;

import android.content.res.AssetFileDescriptor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;
    private static final int INPUT_WIDTH = 224;
    private static final int INPUT_HEIGHT = 224;

    private Interpreter tflite;
    private ImageView imageView;
    private TextView resultTextView;
    private Button browseGalleryButton, captureImageButton, predictButton;
    private List<String> labels;
    private Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        browseGalleryButton = findViewById(R.id.browseGalleryButton);
        captureImageButton = findViewById(R.id.captureImageButton);
        predictButton = findViewById(R.id.predictButton);

        browseGalleryButton.setOnClickListener(v -> openGallery());
        captureImageButton.setOnClickListener(v -> captureImage());
        predictButton.setOnClickListener(v -> {
            if (selectedBitmap != null) {
                runModelInference();
            } else {
                resultTextView.setText("Please select an image first.");
            }
        });

        try {
            tflite = new Interpreter(loadModelFile());
            labels = loadLabels();
        } catch (IOException e) {
            e.printStackTrace();
            resultTextView.setText("Error loading model or labels.");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode != PICK_IMAGE && requestCode != CAPTURE_IMAGE) || resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            if (imageUri == null) {
                resultTextView.setText("No image selected.");
                return;
            }

            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType == null || !mimeType.startsWith("image/")) {
                resultTextView.setText("Unsupported file. Please select an image.");
                return;
            }

            try (InputStream imageStream = getContentResolver().openInputStream(imageUri)) {
                selectedBitmap = BitmapFactory.decodeStream(imageStream);
                if (selectedBitmap == null) {
                    resultTextView.setText("Could not decode selected image.");
                    return;
                }
                imageView.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                resultTextView.setText("Error loading image.");
            }
        } else if (requestCode == CAPTURE_IMAGE) {
            Bundle extras = data.getExtras();
            if (extras == null || !(extras.get("data") instanceof Bitmap)) {
                resultTextView.setText("Failed to capture image.");
                return;
            }
            selectedBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(selectedBitmap);
        }
    }

    private void runModelInference() {
        Bitmap bitmap = ImageUtils.resizeImage(selectedBitmap, INPUT_WIDTH, INPUT_HEIGHT);
        ByteBuffer byteBuffer = ImageUtils.convertBitmapToByteBuffer(bitmap);

        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, INPUT_WIDTH, INPUT_HEIGHT, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(byteBuffer);

        int NUM_CLASSES = labels.size();
        TensorBuffer outputFeature0 = TensorBuffer.createFixedSize(new int[]{1, NUM_CLASSES}, DataType.FLOAT32);

        tflite.run(inputFeature0.getBuffer(), outputFeature0.getBuffer().rewind());

        float[] output = outputFeature0.getFloatArray();
        float maxConfidence = -1;
        int predictedIndex = -1;

        for (int i = 0; i < output.length; i++) {
            if (output[i] > maxConfidence) {
                maxConfidence = output[i];
                predictedIndex = i;
            }
        }

        if (predictedIndex < 0 || predictedIndex >= labels.size()) {
            resultTextView.setText("Inference failed. Invalid prediction index.");
            return;
        }

        String predictedLabel = labels.get(predictedIndex);
        String resultText = String.format(
                Locale.US,
                "Predicted: %s\nConfidence: %.2f%%",
                predictedLabel,
                maxConfidence * 100f
        );

        resultTextView.setText(resultText);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        try (
                AssetFileDescriptor fileDescriptor = getAssets().openFd("dfu_mobilenetv2.tflite");
                FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
                FileChannel fileChannel = inputStream.getChannel()
        ) {
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private List<String> loadLabels() throws IOException {
        List<String> labels = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line);
            }
        }
        return labels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }
}
