package ru.magflayer.spectrum.domain.model.converter;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import ru.magflayer.spectrum.data.database.model.SwatchRealm;

class SwatchRealmConverter {

    List<Integer> fromRealm(List<SwatchRealm> swatchRealms) {
        List<Integer> swatches = new ArrayList<>();

        for (SwatchRealm swatchRealm : swatchRealms) {
            swatches.add(swatchRealm.getColor());
        }

        return swatches;

    }

    RealmList<SwatchRealm> toRealm(List<Integer> colors) {
        RealmList<SwatchRealm> swatchRealmList = new RealmList<>();

        if (colors != null) {
            for (Integer color : colors) {
                swatchRealmList.add(new SwatchRealm(color));
            }
        }

        return swatchRealmList;
    }

}
