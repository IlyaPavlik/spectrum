package ru.magflayer.spectrum.data.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.magflayer.spectrum.data.entity.ColorName;
import ru.magflayer.spectrum.data.entity.NcsColor;
import ru.magflayer.spectrum.data.entity.converter.ColorNameConverter;
import ru.magflayer.spectrum.data.entity.converter.NcsColorConverter;
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity;
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository;

public class ColorInfoRepositoryImpl implements ColorInfoRepository {

    private final AppDatabase appDatabase;
    private final ColorNameConverter colorNameConverter = new ColorNameConverter();
    private final NcsColorConverter ncsColorConverter = new NcsColorConverter();

    public ColorInfoRepositoryImpl(final AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    @Override
    public synchronized boolean uploadColorNames(final Map<String, String> hexName) {
        List<ColorName> colorNames = new ArrayList<>();

        for (Map.Entry<String, String> entry : hexName.entrySet()) {
            colorNames.add(new ColorName(entry.getKey(), entry.getValue()));
        }

        return appDatabase.colorNameDao().saveColorNames(colorNames.toArray(new ColorName[0])).length > 0;
    }

    @Override
    public synchronized boolean isColorNamesUploaded() {
        return appDatabase.colorNameDao().getRowCount() > 0;
    }

    @Override
    public synchronized List<ColorInfoEntity> loadColorNames() {
        List<ColorName> colorNames = appDatabase.colorNameDao().loadColorNames();
        return colorNameConverter.convertToColorInfos(colorNames);
    }

    @Override
    public synchronized String loadColorNameByHex(final String hex) {
        return appDatabase.colorNameDao().loadColorNameByHex(hex).getName();
    }

    @Override
    public synchronized boolean uploadNcsColors(final Map<String, String> hexName) {
        List<NcsColor> ncsColors = new ArrayList<>();

        for (Map.Entry<String, String> entry : hexName.entrySet()) {
            ncsColors.add(new NcsColor(entry.getKey(), entry.getValue()));
        }

        return appDatabase.ncsColorDao().saveNcsColors(ncsColors.toArray(new NcsColor[0])).length > 0;
    }

    @Override
    public synchronized boolean isNcsColorUploaded() {
        return appDatabase.ncsColorDao().getRowCount() > 0;
    }

    @Override
    public synchronized List<ColorInfoEntity> loadNcsColors() {
        List<NcsColor> ncsColors = appDatabase.ncsColorDao().loadNcsColors();
        return ncsColorConverter.convertToColorInfos(ncsColors);
    }

    @Override
    public synchronized String loadNcsColorByHex(final String hex) {
        NcsColor ncsColor = appDatabase.ncsColorDao().loadNcsColorByHex(hex);
        return ncsColor != null ? ncsColor.getName() : null;
    }
}
