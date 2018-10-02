package ru.magflayer.spectrum.presentation.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageAppearance {

    private Boolean showFloatingButton;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PageAppearance pageAppearance = new PageAppearance();


        public Builder showFloatingButton(final Boolean showFloatingButton) {
            pageAppearance.setShowFloatingButton(showFloatingButton);
            return this;
        }

        public PageAppearance build() {
            return pageAppearance;
        }

    }
}
