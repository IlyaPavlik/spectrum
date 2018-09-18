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

}