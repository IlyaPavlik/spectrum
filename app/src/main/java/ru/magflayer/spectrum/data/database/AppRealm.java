package ru.magflayer.spectrum.data.database;

import android.content.Context;
import android.support.annotation.Nullable;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ru.magflayer.spectrum.data.database.model.ColorPictureRealm;
import ru.magflayer.spectrum.domain.converter.ColorPictureRealmConverter;
import ru.magflayer.spectrum.domain.model.event.PictureSavedEvent;
import ru.magflayer.spectrum.domain.model.ColorPicture;

public class AppRealm {

    private Realm realm;
    private Bus bus;

    private ColorPictureRealmConverter colorPictureRealmConverter = new ColorPictureRealmConverter();

    public AppRealm(final Context context, final Bus bus) {
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("realm")
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        this.bus = bus;
    }

    public void open() {
        realm = Realm.getDefaultInstance();
    }

    public void savePicture(final ColorPicture colorPicture) {
        realm.executeTransactionAsync(realm1 -> {
            ColorPictureRealm colorPictureRealm = colorPictureRealmConverter.toRealm(colorPicture);
            realm1.copyToRealmOrUpdate(colorPictureRealm);
        }, () -> bus.post(new PictureSavedEvent()));
    }

    public List<ColorPicture> loadPictures() {
        RealmResults<ColorPictureRealm> result = realm.where(ColorPictureRealm.class).findAll();
        List<ColorPicture> colorPictures = new ArrayList<>();
        for (ColorPictureRealm pictureRealm : result) {
            colorPictures.add(colorPictureRealmConverter.fromRealm(pictureRealm));
        }
        return colorPictures;
    }

    @Nullable
    public ColorPicture loadPicture(final long id) {
        ColorPictureRealm result = realm.where(ColorPictureRealm.class)
                .equalTo("date", id)
                .findFirst();
        return colorPictureRealmConverter.fromRealm(result);
    }

    public void removePicture(ColorPicture colorPicture) {
        realm.executeTransactionAsync(realm1 -> {
            RealmResults<ColorPictureRealm> rows = realm1.where(ColorPictureRealm.class)
                    .equalTo("date", colorPicture.getDateInMillis())
                    .findAll();
            rows.deleteAllFromRealm();
        });
    }

    public void close() {
        realm.close();
    }

}
