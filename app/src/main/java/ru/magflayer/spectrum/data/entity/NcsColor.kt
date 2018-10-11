package ru.magflayer.spectrum.data.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class NcsColor(
        @PrimaryKey var hex: String,
        @ColumnInfo(name = "name") var name: String
)
