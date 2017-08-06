package ru.magflayer.spectrum.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;

import java.util.List;

public class BitmapUtils {

    private BitmapUtils() {
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
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
    public static int averageColor(Bitmap bi, int x0, int y0, int w,
                                   int h) {
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

    public static Palette.Swatch mostPopularColor(Bitmap bi, int x0, int y0, int w,
                                                  int h) {
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

    public static Bitmap createMultiColorHorizontalBitmap(int width, int height, List<Integer> colors) {
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

    public static Bitmap bytesToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}
