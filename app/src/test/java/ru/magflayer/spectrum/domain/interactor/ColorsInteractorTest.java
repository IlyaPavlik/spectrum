package ru.magflayer.spectrum.domain.interactor;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.google.gson.Gson;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ColorsInteractorTest {

    private ColorsInteractor colorsInteractor;
    private String testData = "[{\"id\":\"#FBCEB1\",\"name\":\"Абрикосовый\"}," +
            "{\"id\":\"#FDD9B5\",\"name\":\"Абрикосовый Крайола\"}," +
            "{\"id\":\"#B5B8B1\",\"name\":\"Агатовый серый\"}," +
            "{\"id\":\"#7FFFD4\",\"name\":\"Аквамариновый\"}," +
            "{\"id\":\"#78DBE2\",\"name\":\"Аквамариновый Крайола\"}," +
            "{\"id\":\"#E32636\",\"name\":\"Ализариновый красный\"}," +
            "{\"id\":\"#FF2400\",\"name\":\"Алый\"}," +
            "{\"id\":\"#ED3CCA\",\"name\":\"Амарантовый маджента\"}," +
            "{\"id\":\"#CD9575\",\"name\":\"Античная латунь\"}]";
    private List<ColorInfoEntity> testColorInfos = Arrays.asList(
            new ColorInfoEntity("#FBCEB1", "Абрикосовый"),
            new ColorInfoEntity("#FDD9B5", "Абрикосовый Крайола"),
            new ColorInfoEntity("#B5B8B1", "Агатовый серый"),
            new ColorInfoEntity("#7FFFD4", "Аквамариновый"),
            new ColorInfoEntity("#78DBE2", "Аквамариновый Крайола"),
            new ColorInfoEntity("#E32636", "Ализариновый красный"),
            new ColorInfoEntity("#FF2400", "Алый"),
            new ColorInfoEntity("#ED3CCA", "Амарантовый маджента"),
            new ColorInfoEntity("#CD9575", "Античная латунь")
    );

    @Before
    public void init() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        AssetManager assetManager = mock(AssetManager.class);

        when(context.getResources()).thenReturn(resources);

        ResourceManager resourceManager = new ResourceManager(context);
        InputStream testInputStream = new ByteArrayInputStream(testData.getBytes(StandardCharsets.UTF_8));

        try {
            when(resources.getAssets()).thenReturn(assetManager);
            when(assetManager.open(ColorsInteractor.COLOR_NAMES_ASSET_NAME)).thenReturn(testInputStream);
        } catch (IOException e) {
            Assert.fail("Resource manager getAsset throws exception: " + e.getMessage());
        }

        colorsInteractor = new ColorsInteractor(resourceManager, new Gson());
    }

    @Test
    public void colorNamesTest() {
        List<ColorInfoEntity> exportedColorInfos = new ArrayList<>();
        colorsInteractor.loadColorNames()
                .subscribe(exportedColorInfos::addAll);
        Assert.assertThat(testColorInfos, CoreMatchers.is(exportedColorInfos));
    }

}
