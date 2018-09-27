package ru.magflayer.spectrum.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ColorName {

    @NonNull
    @PrimaryKey
    private String hex;
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    public String getHex() {
        return hex;
    }

    public void setHex(@NonNull final String hex) {
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
