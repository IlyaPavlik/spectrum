package ru.magflayer.colorpointer.data.database.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ColorPictureRealm extends RealmObject {

    @PrimaryKey
    private long date;
    private String pictureBase64;
    private RealmList<SwatchRealm> swatches;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPictureBase64() {
        return pictureBase64;
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }

    public RealmList<SwatchRealm> getSwatches() {
        return swatches;
    }

    public void setSwatches(RealmList<SwatchRealm> swatches) {
        this.swatches = swatches;
    }
}
