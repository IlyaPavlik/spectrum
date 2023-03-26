package ru.magflayer.spectrum

class ColorSpace(
    val color: Int = 0,
    val rgb: RGB,
    val cmyk: CMYK,
    val ryb: RYB,
    val hex: HEX,
    val hsl: HSL,
    val hsv: HSV,
    val xyz: XYZ,
    val cieLab: CIE_LAB,
    val ncs: NCS,
) {

    enum class RGB(val rgb: IntArray) {
        BLACK(intArrayOf(0, 0, 0)),
        WHITE(intArrayOf(255, 255, 255)),
        RED(intArrayOf(255, 0, 0)),
        GREEN(intArrayOf(0, 255, 0)),
        BLUE(intArrayOf(0, 0, 255)),
    }

    enum class CMYK(val cmyk: IntArray) {
        BLACK(intArrayOf(0, 0, 0, 100)),
        WHITE(intArrayOf(0, 0, 0, 0)),
        RED(intArrayOf(0, 100, 100, 0)),
        GREEN(intArrayOf(100, 0, 100, 0)),
        BLUE(intArrayOf(100, 100, 0, 0)),
    }

    enum class RYB(val ryb: IntArray) {
        BLACK(intArrayOf(0, 0, 0)),
        WHITE(intArrayOf(100, 100, 100)),
        RED(intArrayOf(100, 0, 0)),
        GREEN(intArrayOf(0, 100, 100)),
        BLUE(intArrayOf(0, 0, 100)),
    }

    enum class HEX(val hex: String) {
        BLACK("#000000"),
        WHITE("#FFFFFF"),
        RED("#FF0000"),
        GREEN("#00FF00"),
        BLUE("#0000FF"),
    }

    enum class HSL(val hsl: FloatArray) {
        BLACK(floatArrayOf(0f, 0f, 0f)),
        WHITE(floatArrayOf(0f, 0f, 1f)),
        RED(floatArrayOf(0f, 1f, 0.5f)),
        GREEN(floatArrayOf(120f, 1f, 0.5f)),
        BLUE(floatArrayOf(240f, 1f, 0.5f)),
    }

    enum class HSV(val hsl: IntArray) {
        BLACK(intArrayOf(0, 0, 0)),
        WHITE(intArrayOf(0, 0, 100)),
        RED(intArrayOf(0, 100, 100)),
        GREEN(intArrayOf(120, 100, 100)),
        BLUE(intArrayOf(240, 100, 100)),
    }

    enum class XYZ(val xyz: DoubleArray) {
        BLACK(doubleArrayOf(0.0, 0.0, 0.0)),
        WHITE(doubleArrayOf(95.05, 100.0, 108.9)),
        RED(doubleArrayOf(41.24, 21.26, 1.93)),
        GREEN(doubleArrayOf(35.76, 71.52, 11.92)),
        BLUE(doubleArrayOf(18.05, 7.22, 95.05)),
    }

    enum class CIE_LAB(val cieLab: DoubleArray) {
        BLACK(doubleArrayOf(0.0, 0.0, 0.0)),
        WHITE(doubleArrayOf(100.0, 0.0053, -0.0104)),
        RED(doubleArrayOf(53.2329, 80.1093, 67.2201)),
        GREEN(doubleArrayOf(87.737, -86.1846, 83.1812)),
        BLUE(doubleArrayOf(32.3026, 79.1967, -107.8637)),
    }

    enum class NCS(val code: String) {
        BLACK("9000 N"),
        WHITE("0300 N"),
        RED("0585 Y80R"),
        GREEN("0560 G20Y"),
        BLUE("2060 R70B"),
    }
}
