package ru.magflayer.colorpointer.data.database;

import android.content.Context;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ru.magflayer.colorpointer.data.database.model.ColorPictureRealm;
import ru.magflayer.colorpointer.domain.converter.ColorPictureRealmConverter;
import ru.magflayer.colorpointer.domain.event.PictureSavedEvent;
import ru.magflayer.colorpointer.domain.model.ColorPicture;

public class AppRealm {

    private Realm realm;
    private Bus bus;

    private ColorPictureRealmConverter colorPictureRealmConverter = new ColorPictureRealmConverter();

    public AppRealm(Context context, Bus bus) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .name("realm")
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        this.bus = bus;
    }

    public void open() {
        realm = Realm.getDefaultInstance();
    }

    public void savePicture(final ColorPicture colorPicture) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ColorPictureRealm colorPictureRealm = colorPictureRealmConverter.toRealm(colorPicture);
                realm.copyToRealmOrUpdate(colorPictureRealm);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                bus.post(new PictureSavedEvent());
            }
        });
    }

    public List<ColorPicture> loadPictures() {
        RealmResults<ColorPictureRealm> result = realm.where(ColorPictureRealm.class).findAll();
        List<ColorPicture> colorPictures = new ArrayList<>();
        for (ColorPictureRealm pictureRealm : result) {
            colorPictures.add(colorPictureRealmConverter.fromRealm(pictureRealm));
        }
        return colorPictures;
    }

    public void removePicture() {

    }

    public void close() {
        realm.close();
    }

}
