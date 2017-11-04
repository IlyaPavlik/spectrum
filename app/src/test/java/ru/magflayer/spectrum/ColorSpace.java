package ru.magflayer.spectrum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ColorSpace {

    private int color;
    private RGB rgb;
    private CMYK cmyk;
    private RYB ryb;
    private HEX hex;
    private HSL hsl;
    private HSV hsv;
    private XYZ xyz;
    private CIE_LAB cieLab;
    private NCS ncs;

    @Getter
    @AllArgsConstructor
    public enum RGB {
        BLACK(new int[]{0, 0, 0}),
        WHITE(new int[]{255, 255, 255}),
        RED(new int[]{255, 0, 0}),
        GREEN(new int[]{0, 255, 0}),
        BLUE(new int[]{0, 0, 255});

        private int[] rgb;
    }

    @Getter
    @AllArgsConstructor
    public enum CMYK {
        BLACK(new int[]{0, 0, 0, 100}),
        WHITE(new int[]{0, 0, 0, 0}),
        RED(new int[]{0, 100, 100, 0}),
        GREEN(new int[]{100, 0, 100, 0}),
        BLUE(new int[]{100, 100, 0, 0});

        private int[] cmyk;
    }

    @Getter
    @AllArgsConstructor
    public enum RYB {
        BLACK(new int[]{0, 0, 0}),
        WHITE(new int[]{100, 100, 100}),
        RED(new int[]{100, 0, 0}),
        GREEN(new int[]{0, 100, 100}),
        BLUE(new int[]{0, 0, 100});

        private int[] ryb;
    }

    @Getter
    @AllArgsConstructor
    public enum HEX {
        BLACK("#000000"),
        WHITE("#FFFFFF"),
        RED("#FF0000"),
        GREEN("#00FF00"),
        BLUE("#0000FF");

        private String hex;
    }

    @Getter
    @AllArgsConstructor
    public enum HSL {
        BLACK(new float[]{0, 0, 0}),
        WHITE(new float[]{0, 0, 1}),
        RED(new float[]{0, 1, 0.5F}),
        GREEN(new float[]{120, 1, 0.5F}),
        BLUE(new float[]{240, 1, 0.5F});

        private float[] hsl;
    }

    @Getter
    @AllArgsConstructor
    public enum HSV {
        BLACK(new int[]{0, 0, 0}),
        WHITE(new int[]{0, 0, 100}),
        RED(new int[]{0, 100, 100}),
        GREEN(new int[]{120, 100, 100}),
        BLUE(new int[]{240, 100, 100});

        private int[] hsl;
    }

    @Getter
    @AllArgsConstructor
    public enum XYZ {
        BLACK(new double[]{0, 0, 0}),
        WHITE(new double[]{95.05, 100, 108.9}),
        RED(new double[]{41.24, 21.26, 1.93}),
        GREEN(new double[]{35.76, 71.52, 11.92}),
        BLUE(new double[]{18.05, 7.22, 95.05});

        private double[] xyz;
    }

    @Getter
    @AllArgsConstructor
    public enum CIE_LAB {
        BLACK(new double[]{0, 0, 0}),
        WHITE(new double[]{100, 0.0053, -0.0104}),
        RED(new double[]{53.2329, 80.1093, 67.2201}),
        GREEN(new double[]{87.737, -86.1846, 83.1812}),
        BLUE(new double[]{32.3026, 79.1967, -107.8637});

        private double[] cieLab;
    }

    @Getter
    @AllArgsConstructor
    public enum NCS {
        BLACK("9000 N"),
        WHITE("0300 N"),
        RED("0585 Y80R"),
        GREEN("0560 G20Y"),
        BLUE("2060 R70B");

        private String code;
    }
}
