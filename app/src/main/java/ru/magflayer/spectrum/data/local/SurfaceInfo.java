package ru.magflayer.spectrum.data.local;

import android.graphics.Bitmap;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SurfaceInfo {

    private Type type;
    private Bitmap bitmap;

    public enum Type {
        SINGLE, FULL
    }

}
