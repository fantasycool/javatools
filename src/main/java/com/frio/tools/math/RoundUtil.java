package com.frio.tools.math;

/**
 * Created by frio on 17/2/22.
 */
public class RoundUtil {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
