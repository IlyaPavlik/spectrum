package ru.magflayer.spectrum.domain.entity;

public interface AnalyticsEvent {

    String TAKE_PHOTO = "take_photo";
    String TAKE_PHOTO_MODE = "take_photo_mode";
    String TAKE_PHOTO_MODE_SINGLE = "take_photo_mode_single";
    String TAKE_PHOTO_MODE_MULTIPLE = "take_photo_mode_multiple";

    String OPEN_HISTORY = "open_history";
    String OPEN_HISTORY_DETAILS = "open_history_details";

    String CHOOSE_IMAGE = "choose_image";

}
