package ru.magflayer.spectrum.data.android;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ResourceManager {

    private final Resources resources;

    @Inject
    public ResourceManager(final Context context) {
        this.resources = context.getResources();
    }

    public String getString(@StringRes final int id) {
        return resources.getString(id);
    }

    public String getQuantityString(@PluralsRes final int id, final int quantity) {
        return resources.getQuantityString(id, quantity);
    }

    public InputStream getAsset(final String assetName) throws IOException {
        return resources.getAssets().open(assetName);
    }
}
