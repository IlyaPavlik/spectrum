package ru.magflayer.spectrum

import android.graphics.Color
import junit.framework.Assert.assertTrue
import org.junit.Test
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import java.util.*

class ColorTest {

    private val testColorList = object : ArrayList<ColorSpace>() {
        init {
            add(
                ColorSpace(
                    Color.RED,
                    ColorSpace.RGB.RED,
                    ColorSpace.CMYK.RED,
                    ColorSpace.RYB.RED,
                    ColorSpace.HEX.RED,
                    ColorSpace.HSL.RED,
                    ColorSpace.HSV.RED,
                    ColorSpace.XYZ.RED,
                    ColorSpace.CIE_LAB.RED,
                    ColorSpace.NCS.RED,
                ),
            )
            add(
                ColorSpace(
                    Color.GREEN,
                    ColorSpace.RGB.GREEN,
                    ColorSpace.CMYK.GREEN,
                    ColorSpace.RYB.GREEN,
                    ColorSpace.HEX.GREEN,
                    ColorSpace.HSL.GREEN,
                    ColorSpace.HSV.GREEN,
                    ColorSpace.XYZ.GREEN,
                    ColorSpace.CIE_LAB.GREEN,
                    ColorSpace.NCS.GREEN,
                ),
            )
            add(
                ColorSpace(
                    Color.BLUE,
                    ColorSpace.RGB.BLUE,
                    ColorSpace.CMYK.BLUE,
                    ColorSpace.RYB.BLUE,
                    ColorSpace.HEX.BLUE,
                    ColorSpace.HSL.BLUE,
                    ColorSpace.HSV.BLUE,
                    ColorSpace.XYZ.BLUE,
                    ColorSpace.CIE_LAB.BLUE,
                    ColorSpace.NCS.BLUE,
                ),
            )
            add(
                ColorSpace(
                    Color.BLACK,
                    ColorSpace.RGB.BLACK,
                    ColorSpace.CMYK.BLACK,
                    ColorSpace.RYB.BLACK,
                    ColorSpace.HEX.BLACK,
                    ColorSpace.HSL.BLACK,
                    ColorSpace.HSV.BLACK,
                    ColorSpace.XYZ.BLACK,
                    ColorSpace.CIE_LAB.BLACK,
                    ColorSpace.NCS.BLACK,
                ),
            )
            add(
                ColorSpace(
                    Color.WHITE,
                    ColorSpace.RGB.WHITE,
                    ColorSpace.CMYK.WHITE,
                    ColorSpace.RYB.WHITE,
                    ColorSpace.HEX.WHITE,
                    ColorSpace.HSL.WHITE,
                    ColorSpace.HSV.WHITE,
                    ColorSpace.XYZ.WHITE,
                    ColorSpace.CIE_LAB.WHITE,
                    ColorSpace.NCS.WHITE,
                ),
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun rgbTest() {
        for (colorSpace in testColorList) {
            val rgb = ColorHelper.dec2Rgb(colorSpace.color)
            assertTrue(colorSpace.hex.toString(), colorSpace.rgb.rgb.contentEquals(rgb))
        }
    }

    @Test
    fun cmykTest() {
        for (colorSpace in testColorList) {
            val cmyk = ColorHelper.dec2Cmyk(colorSpace.color)
            assertTrue(colorSpace.hex.toString(), colorSpace.cmyk.cmyk.contentEquals(cmyk))
        }
    }

    @Test
    fun hslTest() {
        for (colorSpace in testColorList) {
            val hsl = ColorHelper.dec2Hsl(colorSpace.color)
            assertTrue(colorSpace.hex.toString(), colorSpace.hsl.hsl.contentEquals(hsl))
        }
    }

    @Test
    fun xyzTest() {
        for (colorSpace in testColorList) {
            val xyz = ColorHelper.dec2Xyz(colorSpace.color)
            assertTrue(colorSpace.hex.toString(), colorSpace.xyz.xyz.contentEquals(xyz))
        }
    }

    @Test
    fun labTest() {
        for (colorSpace in testColorList) {
            val ciaLab = ColorHelper.dec2Lab(colorSpace.color)
            assertTrue(colorSpace.hex.toString(), colorSpace.cieLab.cieLab.contentEquals(ciaLab))
        }
    }

    //    @Test
    //    public void ncsTest() {
    //        File file = new File("app/src/main/assets/ncs.json");
    //        System.out.println(file.getAbsolutePath());
    //        try {
    //            InputStream inputStream = new FileInputStream(file);
    //            List<ColorHelper.NcsColorEntity> colors = null;
    //            try {
    //                DataInputStream dataInputStream = new DataInputStream(inputStream);
    //                String data = dataInputStream.readLine();
    //                colors = new Gson().fromJson(data, new TypeToken<List<ColorHelper.NcsColorEntity>>() {
    //                }.getType());
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //
    //            for (ColorSpace colorSpace : testColorList) {
    //                String ncs = ColorHelper.dec2Ncs(colors, Color.BLACK);
    //                System.out.println(ColorSpace.NCS.BLACK.toString() + " NCS: " + ncs);
    //            assertTrue(colorSpace.getHex().toString(), colorSpace.getNcs().getCode().equals(ncs));
    //            }
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        }
    //    }
}
