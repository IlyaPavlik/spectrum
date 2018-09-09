package ru.magflayer.spectrum.data.android;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ResourceManager {

    private Resources resources;

    @Inject
    public ResourceManager(final Context context) {
        this.resources = context.getResources();
    }

    public InputStream getAsset(final String assetName) throws IOException {
        return resources.getAssets().open(assetName);
    }
}
