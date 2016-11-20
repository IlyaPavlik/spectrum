package ru.magflayer.spectrum.domain.converter;

import ru.magflayer.spectrum.data.database.model.ColorPictureRealm;
import ru.magflayer.spectrum.domain.model.ColorPicture;

public class ColorPictureRealmConverter {

    private SwatchRealmConverter swatchRealmConverter = new SwatchRealmConverter();

    public ColorPicture fromRealm(ColorPictureRealm realm) {
        ColorPicture colorPicture = new ColorPicture();

        colorPicture.setDateInMillis(realm.getDate());
        colorPicture.setPictureBase64(realm.getPictureBase64());
        colorPicture.setSwatches(swatchRealmConverter.fromRealm(realm.getSwatches()));

        return colorPicture;
    }

    public ColorPictureRealm toRealm(ColorPicture colorPicture) {
        ColorPictureRealm  pictureRealm = new ColorPictureRealm();

        pictureRealm.setDate(colorPicture.getDateInMillis());
        pictureRealm.setPictureBase64(colorPicture.getPictureBase64());
        pictureRealm.setSwatches(swatchRealmConverter.toRealm(colorPicture.getSwatches()));

        return pictureRealm;
    }

}
