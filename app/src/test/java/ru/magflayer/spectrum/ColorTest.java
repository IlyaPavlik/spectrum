package ru.magflayer.spectrum;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.magflayer.spectrum.utils.ColorUtils;

import static junit.framework.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Color.class)
public class ColorTest {

    private List<ColorSpace> testColorList = new ArrayList<ColorSpace>() {
        {
            add(new ColorSpace(Color.RED, ColorSpace.RGB.RED, ColorSpace.CMYK.RED,
                    ColorSpace.RYB.RED, ColorSpace.HEX.RED, ColorSpace.HSL.RED,
                    ColorSpace.HSV.RED, ColorSpace.XYZ.RED, ColorSpace.CIE_LAB.RED, ColorSpace.NCS.RED));
            add(new ColorSpace(Color.GREEN, ColorSpace.RGB.GREEN, ColorSpace.CMYK.GREEN,
                    ColorSpace.RYB.GREEN, ColorSpace.HEX.GREEN, ColorSpace.HSL.GREEN,
                    ColorSpace.HSV.GREEN, ColorSpace.XYZ.GREEN, ColorSpace.CIE_LAB.GREEN, ColorSpace.NCS.GREEN));
            add(new ColorSpace(Color.BLUE, ColorSpace.RGB.BLUE, ColorSpace.CMYK.BLUE,
                    ColorSpace.RYB.BLUE, ColorSpace.HEX.BLUE, ColorSpace.HSL.BLUE,
                    ColorSpace.HSV.BLUE, ColorSpace.XYZ.BLUE, ColorSpace.CIE_LAB.BLUE, ColorSpace.NCS.BLUE));
            add(new ColorSpace(Color.BLACK, ColorSpace.RGB.BLACK, ColorSpace.CMYK.BLACK,
                    ColorSpace.RYB.BLACK, ColorSpace.HEX.BLACK, ColorSpace.HSL.BLACK,
                    ColorSpace.HSV.BLACK, ColorSpace.XYZ.BLACK, ColorSpace.CIE_LAB.BLACK, ColorSpace.NCS.BLACK));
            add(new ColorSpace(Color.WHITE, ColorSpace.RGB.WHITE, ColorSpace.CMYK.WHITE,
                    ColorSpace.RYB.WHITE, ColorSpace.HEX.WHITE, ColorSpace.HSL.WHITE,
                    ColorSpace.HSV.WHITE, ColorSpace.XYZ.WHITE, ColorSpace.CIE_LAB.WHITE, ColorSpace.NCS.WHITE));
        }
    };

    @Before
    public void init() {
        PowerMockito.mockStatic(Color.class);
    }

    @Test
    public void rgbTest() throws Exception {
        for (ColorSpace colorSpace : testColorList) {
            int[] rgb = ColorUtils.dec2Rgb(colorSpace.getColor());
            assertTrue(colorSpace.getHex().toString(), Arrays.equals(colorSpace.getRgb().getRgb(), rgb));
        }
    }

    @Test
    public void cmykTest() {
        for (ColorSpace colorSpace : testColorList) {
            int[] cmyk = ColorUtils.dec2Cmyk(colorSpace.getColor());
            assertTrue(colorSpace.getHex().toString(), Arrays.equals(colorSpace.getCmyk().getCmyk(), cmyk));
        }
    }

    @Test
    public void hslTest() {
        for (ColorSpace colorSpace : testColorList) {
            float[] hsl = ColorUtils.dec2Hsl(colorSpace.getColor());
            assertTrue(colorSpace.getHex().toString(), Arrays.equals(colorSpace.getHsl().getHsl(), hsl));
        }
    }

    @Test
    public void xyzTest() {
        for (ColorSpace colorSpace : testColorList) {
            double[] xyz = ColorUtils.dec2Xyz(colorSpace.getColor());
            assertTrue(colorSpace.getHex().toString(), Arrays.equals(colorSpace.getXyz().getXyz(), xyz));
        }
    }

    @Test
    public void labTest() {
        for (ColorSpace colorSpace : testColorList) {
            double[] ciaLab = ColorUtils.dec2Lab(colorSpace.getColor());
            assertTrue(colorSpace.getHex().toString(), Arrays.equals(colorSpace.getCieLab().getCieLab(), ciaLab));
        }
    }

//    @Test
//    public void ncsTest() {
//        File file = new File("app/src/main/assets/ncs.json");
//        System.out.println(file.getAbsolutePath());
//        try {
//            InputStream inputStream = new FileInputStream(file);
//            List<ColorUtils.NcsColor> colors = null;
//            try {
//                DataInputStream dataInputStream = new DataInputStream(inputStream);
//                String data = dataInputStream.readLine();
//                colors = new Gson().fromJson(data, new TypeToken<List<ColorUtils.NcsColor>>() {
//                }.getType());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            for (ColorSpace colorSpace : testColorList) {
//                String ncs = ColorUtils.dec2Ncs(colors, Color.BLACK);
//                System.out.println(ColorSpace.NCS.BLACK.toString() + " NCS: " + ncs);
//            assertTrue(colorSpace.getHex().toString(), colorSpace.getNcs().getCode().equals(ncs));
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}