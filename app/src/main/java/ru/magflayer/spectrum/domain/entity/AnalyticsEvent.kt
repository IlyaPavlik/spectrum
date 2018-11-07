package ru.magflayer.spectrum.domain.entity

interface AnalyticsEvent {
    companion object {

        const val TAKE_PHOTO = "take_photo"
        const val TAKE_PHOTO_MODE = "take_photo_mode"
        const val TAKE_PHOTO_MODE_SINGLE = "take_photo_mode_single"
        const val TAKE_PHOTO_MODE_MULTIPLE = "take_photo_mode_multiple"
        const val TAKE_PHOTO_FLASHLIGHT = "take_photo_flashlight"
        const val TAKE_PHOTO_ZOOM = "take_photo_zoom"

        const val OPEN_HISTORY = "open_history"
        const val OPEN_HISTORY_DETAILS = "open_history_details"

        const val CHOOSE_IMAGE = "choose_image"
    }

}
