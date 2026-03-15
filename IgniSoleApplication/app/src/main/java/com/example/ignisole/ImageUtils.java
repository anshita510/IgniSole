package com.example.ignisole;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageUtils {
    public static Bitmap resizeImage(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * width * height * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width, height);
        int pixel = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 127.5f - 1.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 127.5f - 1.0f);
                byteBuffer.putFloat((val & 0xFF) / 127.5f - 1.0f);
            }
        }
        return byteBuffer;
    }
}