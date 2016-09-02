package ru.magflayer.colorpointer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Base64Utils {

    private Base64Utils() {
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String pictureBase64) {
        byte[] decodedString = base46ToBytes(pictureBase64);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static byte[] base46ToBytes(String pictureBase64) {
        return Base64.decode(pictureBase64, Base64.DEFAULT);
    }

}
