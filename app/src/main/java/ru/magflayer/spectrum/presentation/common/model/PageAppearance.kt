package ru.magflayer.spectrum.presentation.common.model

data class PageAppearance(
        var showFloatingButton: Boolean? = false
) {

    class Builder {
        private val pageAppearance = PageAppearance()


        fun showFloatingButton(showFloatingButton: Boolean?): Builder {
            pageAppearance.showFloatingButton = showFloatingButton
            return this
        }

        fun build(): PageAppearance {
            return pageAppearance
        }

    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
