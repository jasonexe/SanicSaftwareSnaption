package com.snaptiongame.snaption.utilities;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.servercalls.FirebaseReporter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

    // Calculates what inSampleSize to use when loading an image from the phone, which downscales it
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static byte[] decodeSampledBitmapFromStream(ParcelFileDescriptor pfd,
                                                       int reqWidth, int reqHeight) {
        byte[] data = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        FileDescriptor descriptor = pfd.getFileDescriptor();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(descriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFileDescriptor(descriptor, null, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESSION_VALUE, baos);
        data = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            FirebaseReporter.reportException(e, "Couldn't close output stream");
            e.printStackTrace();
        }

        return data;
    }

    public static double getUriAspectRatio(ParcelFileDescriptor pfd) throws FileNotFoundException {
        // Upload photo to storage
        BitmapFactory.Options fileInfo = new BitmapFactory.Options();
        // Only need the dimensions
        fileInfo.inJustDecodeBounds = true;
        if(pfd.getFileDescriptor() != null) {
            BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, fileInfo);
        } else {
            throw new FileNotFoundException();
        }

        double height = fileInfo.outHeight;
        double width = fileInfo.outWidth;
        // Ratio is width/height of the image, 16:9 would be a 1920 x 1080 image, etc.
        double ratio = width / height;
        // Can also get the MIME type in here for Gifs? fileInfo.outMimeType
        return ratio;
    }
}
