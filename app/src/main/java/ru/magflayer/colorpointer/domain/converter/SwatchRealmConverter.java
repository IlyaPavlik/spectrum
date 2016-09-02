package ru.magflayer.colorpointer.domain.converter;

import android.support.v7.graphics.Palette;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import ru.magflayer.colorpointer.data.database.model.SwatchRealm;

public class SwatchRealmConverter {

    public List<Palette.Swatch> fromRealm(List<SwatchRealm> swatchRealms) {
        List<Palette.Swatch> swatches = new ArrayList<>();

        for (SwatchRealm swatchRealm : swatchRealms) {
            swatches.add(new Palette.Swatch(swatchRealm.getColor(), Integer.MAX_VALUE));
        }

        return swatches;

    }

    public RealmList<SwatchRealm> toRealm(List<Palette.Swatch> swatches) {
        RealmList<SwatchRealm> swatchRealmList = new RealmList<>();

        for (Palette.Swatch swatch : swatches) {
            swatchRealmList.add(new SwatchRealm(swatch.getRgb()));
        }

        return swatchRealmList;
    }

}
