package ru.magflayer.spectrum.data.entity

import android.arch.persistence.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class ColorPhoto(
        @field:PrimaryKey var id: Long,
        @field:ColumnInfo(name = "file_path") var filePath: String,
        @field:ColumnInfo(name = "colors") @field:TypeConverters(RgbColorsConverters::class) var rgbColors: List<Int>,
        @field:ColumnInfo(name = "type") var type: Int
) {

    internal class RgbColorsConverters {

        private val gson = Gson()

        @TypeConverter
        fun stringToColorList(data: String?): List<Int> {
            if (data == null) {
                return emptyList()
            }

            val listType = object : TypeToken<List<Int>>() {

            }.type
            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun colorListToString(colors: List<Int>): String {
            return gson.toJson(colors)
        }
    }
}
