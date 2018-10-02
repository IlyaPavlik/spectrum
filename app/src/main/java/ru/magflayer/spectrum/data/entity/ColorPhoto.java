package ru.magflayer.spectrum.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Entity
public class ColorPhoto {

    @PrimaryKey
    private long id;
    @ColumnInfo(name = "file_path")
    private String filePath;
    @ColumnInfo(name = "colors")
    @TypeConverters(RgbColorsConverters.class)
    private List<Integer> rgbColors;

    public ColorPhoto(final long id, final String filePath, final List<Integer> rgbColors) {
        this.id = id;
        this.filePath = filePath;
        this.rgbColors = rgbColors;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public List<Integer> getRgbColors() {
        return rgbColors;
    }

    public void setRgbColors(final List<Integer> rgbColors) {
        this.rgbColors = rgbColors;
    }

    public static class RgbColorsConverters {

        private final Gson gson = new Gson();

        @TypeConverter
        public List<Integer> stringToColorList(final String data) {
            if (data == null) {
                return Collections.emptyList();
            }

            Type listType = new TypeToken<List<Integer>>() {
            }.getType();
            return gson.fromJson(data, listType);
        }

        @TypeConverter
        public String colorListToString(final List<Integer> colors) {
            return gson.toJson(colors);
        }
    }
}
