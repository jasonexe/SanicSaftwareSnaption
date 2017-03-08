package com.snaptiongame.snaptionapp.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.snaptiongame.snaptionapp.servercalls.FirebaseReporter;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by jason_000 on 2/21/2017.
 */

public class BitmapConverter {
    // StackOverflow code to convert drawable to a bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] getImageFromUri(final Uri imageUri, final Activity activity) {
        byte[] data = null;

        try {
            InputStream stream = activity.getContentResolver().openInputStream(imageUri);
            if(stream != null) {
                data = IOUtils.toByteArray(stream);
                stream.close();
            }
        }
        catch (IOException e) {
            FirebaseReporter.reportException(e, "Couldn't find photo after user selected it");
            e.printStackTrace();
        }
        return data;
    }
}
