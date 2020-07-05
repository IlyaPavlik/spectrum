package ru.magflayer.spectrum.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NcsColor(
        @PrimaryKey var hex: String,
        @ColumnInfo(name = "name") var name: String
)
