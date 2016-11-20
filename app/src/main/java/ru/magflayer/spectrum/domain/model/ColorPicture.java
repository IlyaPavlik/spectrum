package ru.magflayer.spectrum.domain.model;

import android.support.v7.graphics.Palette;

import java.util.List;

public class ColorPicture {

    private long dateInMillis;
    private String pictureBase64;
    private List<Palette.Swatch> swatches;

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public String getPictureBase64() {
        return pictureBase64;
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }

    public List<Palette.Swatch> getSwatches() {
        return swatches;
    }

    public void setSwatches(List<Palette.Swatch> swatches) {
        this.swatches = swatches;
    }
}
