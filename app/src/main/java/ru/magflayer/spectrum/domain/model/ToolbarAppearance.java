package ru.magflayer.spectrum.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolbarAppearance {

    public enum Visibility {
        VISIBLE, INVISIBLE, NO_INFLUENCE
    }

    private Visibility visibility;
    private String title;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ToolbarAppearance toolbarAppearance = new ToolbarAppearance();

        public Builder visible(Visibility visibility) {
            toolbarAppearance.setVisibility(visibility);
            return this;
        }

        public Builder title(String title) {
            toolbarAppearance.setTitle(title);
            return this;
        }

        public ToolbarAppearance build() {
            return toolbarAppearance;
        }

    }
}
