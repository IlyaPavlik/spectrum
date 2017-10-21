package ru.magflayer.spectrum.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToolbarAppearance {

    public enum Visibility {
        VISIBLE, INVISIBLE, NO_INFLUENCE
    }

    private Visibility visible;
    private String title;
}
