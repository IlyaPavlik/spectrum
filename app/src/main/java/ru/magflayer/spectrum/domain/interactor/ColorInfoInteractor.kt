package ru.magflayer.spectrum.domain.interactor

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity
import ru.magflayer.spectrum.domain.entity.ColorInfoState
import ru.magflayer.spectrum.domain.entity.ListType
import ru.magflayer.spectrum.domain.entity.NcsColorEntity
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils
import rx.Observable
import rx.functions.Func1
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColorInfoInteractor @Inject
internal constructor(private val colorInfoRepository: ColorInfoRepository,
                     private val resourceManager: ResourceManager,
                     private val gson: Gson) {

    companion object {
        private const val COLOR_NAMES_ASSET_NAME = "colors.json"
        private const val NCS_COLORS_ASSET_NAME = "ncs.json"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    fun uploadColorInfo(): Observable<ColorInfoState> {
        val observables = Arrays.asList(
                uploadColorNamesObservable(),
                uploadNcsColorsObservable()
        )
        return Observable.merge(observables)
    }

    fun findColorNameByHex(hex: String): Observable<String> {
        val sourceColorRgb: IntArray
        try {
            val sourceColor = ColorUtils.parseHex2Dec(hex)
            sourceColorRgb = ColorUtils.dec2Rgb(sourceColor)
        } catch (e: Exception) {
            return Observable.error(e)
        }

        return Observable.fromCallable<List<ColorInfoEntity>> { colorInfoRepository.loadColorNames() }
                .flatMap { Observable.from(it) }
                .map { it.id }
                .reduce(ColorError("", java.lang.Double.MAX_VALUE)) { currentMin, s ->
                    var colorNameHex = s
                    if (colorNameHex.isEmpty()) colorNameHex = "#000000"

                    val ncsColor = ColorUtils.parseHex2Dec(colorNameHex)
                    val ncsColorRgb = ColorUtils.dec2Rgb(ncsColor)

                    val error = calculateColorDifference(sourceColorRgb, ncsColorRgb)
                    val result = if (currentMin.error > error) error else currentMin.error
                    val colorHex = if (currentMin.error > error) colorNameHex else currentMin.id
                    ColorError(colorHex, result)
                }
                .filter { currentMin -> currentMin.error != Integer.MAX_VALUE.toDouble() }
                .map { stringIntegerPair -> stringIntegerPair.id }
                .flatMap { h -> Observable.fromCallable { colorInfoRepository.loadColorNameByHex(h) } }
    }

    fun findNcsColorByHex(hex: String): Observable<String> {
        val sourceColorRgb: IntArray
        try {
            val sourceColor = ColorUtils.parseHex2Dec(hex)
            sourceColorRgb = ColorUtils.dec2Rgb(sourceColor)
        } catch (e: Exception) {
            return Observable.error(e)
        }

        return Observable.fromCallable<List<ColorInfoEntity>> { colorInfoRepository.loadNcsColors() }
                .flatMap { Observable.from(it) }
                .map { it.id }
                .reduce(ColorError("", java.lang.Double.MAX_VALUE)) { currentMin, s ->
                    val ncsColor = ColorUtils.parseHex2Dec(s)
                    val ncsColorRgb = ColorUtils.dec2Rgb(ncsColor)

                    val error = calculateColorDifference(sourceColorRgb, ncsColorRgb)
                    val result = if (currentMin.error > error) error else currentMin.error
                    val colorHex = if (currentMin.error > error) s else currentMin.id
                    ColorError(colorHex, result)
                }
                .filter { currentMin -> currentMin.error != Integer.MAX_VALUE.toDouble() }
                .map { stringIntegerPair -> stringIntegerPair.id }
                .flatMap { h -> Observable.fromCallable { colorInfoRepository.loadNcsColorByHex(h) } }
    }

    private fun uploadColorNamesObservable(): Observable<ColorInfoState> {
        return Observable.fromCallable<Boolean> { colorInfoRepository.isColorNamesUploaded() }
                .flatMap { colorNamesUploaded ->
                    if (colorNamesUploaded) {
                        Observable.just(ColorInfoState.SUCCESS)
                    } else {
                        loadColorNames()
                                .flatMap { Observable.from(it) }
                                .toMap({ it.id }, { it.name })
                                .map { hexName ->
                                    if (colorInfoRepository.uploadColorNames(hexName))
                                        ColorInfoState.SUCCESS
                                    else
                                        ColorInfoState.ERROR
                                }
                    }
                }
                .onErrorReturn { error ->
                    log.warn("Error while uploading color names: ", error)
                    ColorInfoState.ERROR
                }
    }

    private fun uploadNcsColorsObservable(): Observable<ColorInfoState> {
        return Observable.fromCallable<Boolean> { colorInfoRepository.isNcsColorUploaded() }
                .flatMap { ncsColorUploaded ->
                    if (ncsColorUploaded) {
                        Observable.just(ColorInfoState.SUCCESS)
                    } else {
                        loadNscColors()
                                .flatMap { Observable.from(it) }
                                .toMap({ it.value }, { it.name })
                                .map { hexName ->
                                    if (colorInfoRepository.uploadNcsColors(hexName))
                                        ColorInfoState.SUCCESS
                                    else
                                        ColorInfoState.ERROR
                                }
                    }
                }
                .onErrorReturn { error ->
                    log.warn("Error while uploading ncs colors: ", error)
                    ColorInfoState.ERROR
                }
    }

    private fun loadColorNames(): Observable<List<ColorInfoEntity>> {
        return Observable.fromCallable { resourceManager.getAsset(COLOR_NAMES_ASSET_NAME) }
                .flatMap(convertInputStreamToString())
                .map(fromJsonToList(ColorInfoEntity::class.java))
    }

    private fun loadNscColors(): Observable<List<NcsColorEntity>> {
        return Observable.fromCallable { resourceManager.getAsset(NCS_COLORS_ASSET_NAME) }
                .flatMap(convertInputStreamToString())
                .map(fromJsonToList(NcsColorEntity::class.java))
    }

    private fun convertInputStreamToString(): Func1<InputStream, Observable<String>> {
        return Func1 {
            Observable.fromCallable {
                val size = it.available()
                val buffer = ByteArray(size)
                it.read(buffer)
                it.close()
                String(buffer, Charset.forName("UTF-8"))
            }
        }
    }

    private fun <T> fromJsonToList(clazz: Class<T>): Func1<String, List<T>> {
        return Func1 { gson.fromJson<List<T>>(it, ListType(clazz)) }
    }

    private fun calculateColorDifference(colorOneRgb: IntArray, colorTwoRgb: IntArray): Double {
        val error = (Math.pow((colorTwoRgb[0] - colorOneRgb[0]).toDouble(), 2.0)
                + Math.pow((colorTwoRgb[1] - colorOneRgb[1]).toDouble(), 2.0)
                + Math.pow((colorTwoRgb[2] - colorOneRgb[2]).toDouble(), 2.0))
        return Math.sqrt(error)
    }

    private class ColorError internal constructor(val id: String, val error: Double)
}