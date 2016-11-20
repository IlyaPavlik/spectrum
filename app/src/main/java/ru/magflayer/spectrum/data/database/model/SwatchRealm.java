package ru.magflayer.spectrum.data.database.model;

import io.realm.RealmObject;

public class SwatchRealm extends RealmObject {

    private int color;

    public SwatchRealm() {
    }

    public SwatchRealm(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
