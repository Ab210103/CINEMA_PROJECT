package com.example.cinema_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class Utils {

    /**
     * Convert drawable resource (int) ke byte[]
     * @param context Context
     * @param resId drawable resource ID (R.drawable.xxx)
     * @return byte array dari drawable
     */
    public static byte[] drawableToByte(Context context, int resId) {
        // Decode drawable resource jadi bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

        // Convert bitmap ke byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

    /**
     * Convert byte[] ke bitmap
     * @param bytes byte array
     * @return Bitmap
     */
    public static Bitmap byteToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
