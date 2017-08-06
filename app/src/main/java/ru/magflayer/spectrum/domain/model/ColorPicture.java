package ru.magflayer.spectrum.domain.model;

import android.os.Parcel;
import android.os.Parcelable;
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
public class ColorPicture implements Parcelable {

    private long dateInMillis;
    private String pictureBase64;
    private List<Integer> rgbColors;

    private ColorPicture(Parcel in) {
        dateInMillis = in.readLong();
        pictureBase64 = in.readString();
    }

    public static final Creator<ColorPicture> CREATOR = new Creator<ColorPicture>() {
        @Override
        public ColorPicture createFromParcel(Parcel in) {
            return new ColorPicture(in);
        }

        @Override
        public ColorPicture[] newArray(int size) {
            return new ColorPicture[size];
        }
    };

    public static ColorPicture fromBase64(String pictureBase64, List<Palette.Swatch> swatches) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dateInMillis);
        dest.writeString(pictureBase64);
    }
}
