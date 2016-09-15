package ru.magflayer.colorpointer.domain.model;

public class PageAppearance {

    private boolean showFloatingButton;

    private boolean showToolbar;

    public boolean isShowFloatingButton() {
        return showFloatingButton;
    }

    public void setShowFloatingButton(boolean showFloatingButton) {
        this.showFloatingButton = showFloatingButton;
    }

    public boolean isShowToolbar() {
        return showToolbar;
    }

    public void setShowToolbar(boolean showToolbar) {
        this.showToolbar = showToolbar;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PageAppearance pageAppearance = new PageAppearance();


        public Builder showFloatingButton(boolean showFloatingButton) {
            pageAppearance.setShowFloatingButton(showFloatingButton);
            return this;
        }

        public Builder showToolbar(boolean showToolbar) {
            pageAppearance.setShowToolbar(showToolbar);
            return this;
        }

        public PageAppearance build() {
            return pageAppearance;
        }

    }
}
