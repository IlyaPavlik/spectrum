package ru.magflayer.spectrum.presentation.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public final class BitmapUtils {

    private BitmapUtils() {
    }

    public static Bitmap drawableToBitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /*
     * Where bi is your image, (x0,y0) is your upper left coordinate, and (w,h)
     * are your width and height respectively
     */
    public static int averageColor(final Bitmap bi, final int x0, final int y0,
                                   final int w, final int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        int sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                int pixelColor = bi.getPixel(x, y);
                sumr += Color.red(pixelColor);
                sumg += Color.green(pixelColor);
                sumb += Color.blue(pixelColor);
            }
        }
        int num = w * h;
        return Color.rgb(sumr / num, sumg / num, sumb / num);
    }

    public static Palette.Swatch mostPopularColor(final Bitmap bi, final int x0, final int y0,
                                                  final int w, final int h) {
        Bitmap bitmap = Bitmap.createBitmap(bi, x0, y0, w, h);
        List<Palette.Swatch> colors = Palette.from(bitmap).generate().getSwatches();
        Palette.Swatch popularColor = null;
        int countPopularColors = 0;

        for (Palette.Swatch swatch : colors) {
            if (countPopularColors < swatch.getPopulation()) {
                popularColor = swatch;
            }
        }

        return popularColor;
    }

    public static Bitmap createMultiColorHorizontalBitmap(final int width, final int height, final List<Integer> colors) {
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        int colorSize = colors.size() > 0 ? colors.size() : 1;
        int colorWidth = width / colorSize;
        int startX = 0;

        for (Integer color : colors) {
            paint.setColor(color);
            RectF rect = new RectF(startX, 0, startX + colorWidth, height);
            canvas.drawRect(rect, paint);
            startX += colorWidth;
        }

        return resultBitmap;
    }

    public static byte[] convertBitmapToBytes(final Bitmap bitmap) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap bytesToBitmap(final byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}
