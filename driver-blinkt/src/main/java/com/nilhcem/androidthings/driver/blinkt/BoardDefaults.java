package com.nilhcem.androidthings.driver.blinkt;

import android.os.Build;

@SuppressWarnings("WeakerAccess")
public class BoardDefaults {

    private static final String DEVICE_RPI3 = "rpi3";
    private static final String DEVICE_IMX7D_PICO = "imx7d_pico";

    /**
     * Return the preferred data port for each board.
     */
    public static String getDataPort() {
        switch (Build.DEVICE) {
            case DEVICE_RPI3:
                return "BCM23";
            case DEVICE_IMX7D_PICO:
                return "GPIO6_IO13";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Return the preferred clock port for each board.
     */
    public static String getClockPort() {
        switch (Build.DEVICE) {
            case DEVICE_RPI3:
                return "BCM24";
            case DEVICE_IMX7D_PICO:
                return "GPIO6_IO12";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
