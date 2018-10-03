package ru.magflayer.spectrum.domain.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorPhotoEntity {

    private String filePath;
    private List<Integer> rgbColors;
    private long millis;
    private Type type;

    public ColorPhotoEntity(final Type type, final String filePath, final List<Integer> rgbColors) {
        this.type = type;
        this.filePath = filePath;
        this.rgbColors = rgbColors;
        this.millis = System.currentTimeMillis();
    }

    public enum Type {
        INTERNAL, EXTERNAL
    }
}
