package ru.magflayer.spectrum.domain.model;

import android.support.v7.graphics.Palette;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ColorPicture {

    private long dateInMillis;
    private String pictureBase64;
    private List<Integer> rgbColors;

    public static ColorPicture fromBase64(final String pictureBase64, final List<Palette.Swatch> swatches) {
        ColorPicture colorPicture = new ColorPicture();
        colorPicture.setDateInMillis(new Date().getTime());
        colorPicture.setPictureBase64(pictureBase64);

        final List<Integer> colors = new ArrayList<>();
        for (Palette.Swatch swatch : swatches) {
            colors.add(swatch.getRgb());
        }
        colorPicture.setRgbColors(colors);

        return colorPicture;
    }

}
