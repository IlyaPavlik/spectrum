package ru.magflayer.spectrum.domain.interactor

import com.google.gson.Gson
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity
import ru.magflayer.spectrum.domain.entity.ColorInfoState
import ru.magflayer.spectrum.domain.entity.ListType
import ru.magflayer.spectrum.domain.entity.NcsColorEntity
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import java.io.InputStream
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sqrt

@Singleton
class ColorInfoInteractor @Inject constructor(
    private val colorInfoRepository: ColorInfoRepository,
    private val resourceManager: ResourceManager,
    private val gson: Gson
) {

    companion object {
        private const val COLOR_NAMES_ASSET_NAME = "colors.json"
        private const val NCS_COLORS_ASSET_NAME = "ncs.json"
    }

    suspend fun uploadColorInfo(): ColorInfoState {
        val colorNamesState = uploadColorNames()
        val ncsColorsState = uploadNcsColors()
        return if (colorNamesState == ColorInfoState.SUCCESS && ncsColorsState == ColorInfoState.SUCCESS) {
            ColorInfoState.SUCCESS
        } else {
            ColorInfoState.ERROR
        }
    }

    suspend fun findColorNameByHex(hex: String): String {
        val sourceColor = ColorHelper.parseHex2Dec(hex)
        val sourceColorRgb = ColorHelper.dec2Rgb(sourceColor)
        val colorError = colorInfoRepository.loadColorNames()
            .map { ColorError(it.id, java.lang.Double.MAX_VALUE) }
            .reduce { currentMin, colorError ->
                var colorNameHex = colorError.id
                if (colorNameHex.isEmpty()) colorNameHex = "#000000"

                val ncsColor = ColorHelper.parseHex2Dec(colorNameHex)
                val ncsColorRgb = ColorHelper.dec2Rgb(ncsColor)

                val error = calculateColorDifference(sourceColorRgb, ncsColorRgb)
                val result = if (currentMin.error > error) error else currentMin.error
                val colorHex = if (currentMin.error > error) colorNameHex else currentMin.id
                ColorError(colorHex, result)
            }

        return if (colorError.error != Integer.MAX_VALUE.toDouble()) {
            colorInfoRepository.loadColorNameByHex(colorError.id)
        } else {
            throw Exception("Color name is not found")
        }
    }

    suspend fun findNcsColorByHex(hex: String): String {
        val sourceColor = ColorHelper.parseHex2Dec(hex)
        val sourceColorRgb = ColorHelper.dec2Rgb(sourceColor)
        val colorError = colorInfoRepository.loadNcsColors()
            .map { ColorError(it.id, java.lang.Double.MAX_VALUE) }
            .reduce { currentMin, colorError ->
                val ncsColor = ColorHelper.parseHex2Dec(colorError.id)
                val ncsColorRgb = ColorHelper.dec2Rgb(ncsColor)

                val error = calculateColorDifference(sourceColorRgb, ncsColorRgb)
                val result = if (currentMin.error > error) error else currentMin.error
                val colorHex = if (currentMin.error > error) colorError.id else currentMin.id
                ColorError(colorHex, result)
            }

        return if (colorError.error != Integer.MAX_VALUE.toDouble()) {
            colorInfoRepository.loadNcsColorByHex(colorError.id)
        } else {
            throw Exception("NCS color is not found")
        }
    }

    private suspend fun uploadColorNames(): ColorInfoState {
        val colorNamesUploaded = colorInfoRepository.isColorNamesUploaded()
        return if (colorNamesUploaded) {
            ColorInfoState.SUCCESS
        } else {
            val colorNames = loadColorNames().map { it.id to it.name }.toMap()
            if (colorInfoRepository.uploadColorNames(colorNames)) {
                ColorInfoState.SUCCESS
            } else {
                ColorInfoState.ERROR
            }
        }
    }

    private suspend fun uploadNcsColors(): ColorInfoState {
        val ncsColorsUploaded = colorInfoRepository.isNcsColorUploaded()
        return if (ncsColorsUploaded) {
            ColorInfoState.SUCCESS
        } else {
            val nscColors = loadNscColors().map { it.value to it.name }.toMap()

            if (colorInfoRepository.uploadNcsColors(nscColors)) {
                ColorInfoState.SUCCESS
            } else {
                ColorInfoState.ERROR
            }
        }
    }

    private fun loadColorNames(): List<ColorInfoEntity> {
        val inputStream = resourceManager.getAsset(COLOR_NAMES_ASSET_NAME)
        val jsonColorNames = convertInputStreamToString(inputStream)
        return gson.fromJson(jsonColorNames, ListType(ColorInfoEntity::class.java))
    }

    private fun loadNscColors(): List<NcsColorEntity> {
        val inputStream = resourceManager.getAsset(NCS_COLORS_ASSET_NAME)
        val jsonNcsColors = convertInputStreamToString(inputStream)
        return gson.fromJson(jsonNcsColors, ListType(NcsColorEntity::class.java))
    }

    private fun convertInputStreamToString(inputStream: InputStream): String {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charset.forName("UTF-8"))
    }

    private fun calculateColorDifference(colorOneRgb: IntArray, colorTwoRgb: IntArray): Double {
        val error = ((colorTwoRgb[0] - colorOneRgb[0]).toDouble().pow(2.0)
                + (colorTwoRgb[1] - colorOneRgb[1]).toDouble().pow(2.0)
                + (colorTwoRgb[2] - colorOneRgb[2]).toDouble().pow(2.0))
        return sqrt(error)
    }

    private class ColorError(val id: String, val error: Double)
}