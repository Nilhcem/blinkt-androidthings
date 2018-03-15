package com.nilhcem.androidthings.driver.blinkt;

import android.graphics.Color;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.Closeable;
import java.io.IOException;

public class Blinkt implements Closeable {

    /**
     * The direction to apply colors when writing LED data
     */
    public enum Direction {
        NORMAL,
        REVERSED,
    }

    public static final int LEDSTRIP_LENGTH = 8;
    public static final int MAX_BRIGHTNESS = 31;

    private final Gpio dataGpio;
    private final Gpio clockGpio;

    private final int[] colors = new int[LEDSTRIP_LENGTH];
    private int brightness = MAX_BRIGHTNESS >> 1; // default to half
    private Direction direction = Direction.NORMAL;

    /**
     * Creates a new Blinkt driver using {@link BoardDefaults} values.
     */
    public Blinkt() throws IOException {
        this(BoardDefaults.getDataPort(), BoardDefaults.getClockPort());
    }

    /**
     * Create a new Apa102 driver.
     *
     * @param dataGpioName  Name of the data GPIO pin
     * @param clockGpioName Name of the clock GPIO pin
     */
    public Blinkt(String dataGpioName, String clockGpioName) throws IOException {
        PeripheralManager manager = PeripheralManager.getInstance();

        dataGpio = manager.openGpio(dataGpioName);
        dataGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        dataGpio.setActiveType(Gpio.ACTIVE_HIGH);

        clockGpio = manager.openGpio(clockGpioName);
        clockGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        clockGpio.setActiveType(Gpio.ACTIVE_HIGH);

        show();
    }

    /**
     * Releases the GPIOs and related resources.
     */
    @Override
    public void close() throws IOException {
        dataGpio.close();
        clockGpio.close();
    }

    /**
     * Sets the brightness for all LEDs in the strip.
     *
     * @param ledBrightness The brightness of the LED strip, between 0 and {@link #MAX_BRIGHTNESS}.
     */
    public void setBrightness(int ledBrightness) {
        if (ledBrightness < 0 || ledBrightness > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException("Brightness needs to be between 0 and " + MAX_BRIGHTNESS);
        }
        brightness = ledBrightness;
    }

    /**
     * Get the current brightness level
     */
    public int getBrightness() {
        return brightness;
    }

    /**
     * Sets the direction of the LED strip.
     *
     * @param direction The direction of the LED strip, corresponding to {@link Direction}.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Get the current {@link Direction}
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Writes the current RGB Led data to the device.
     *
     * @param colors An array of integers corresponding to a {@link Color}.
     * @throws IOException
     */
    public void write(int[] colors) throws IOException {
        for (int i = 0; i < Math.min(colors.length, LEDSTRIP_LENGTH); i++) {
            setColor(i, colors[i]);
        }
        show();
    }

    /**
     * Sets a color individually in memory.
     * <p>
     * You must call the {@code show} method afterwards to display the colors on the device.
     *
     * @param index index of the color (0 to {@code LEDSTRIP_LENGTH})
     * @param color an integer corresponding to a {@link Color}
     */
    public void setColor(int index, int color) {
        setColor(index, color, brightness);
    }

    /**
     * Sets a color individually in memory, specifying the brightness of the LED
     * <p>
     * You must call the {@code show} method afterwards to display the colors on the device.
     *
     * @param index      index of the color (0 to {@code LEDSTRIP_LENGTH})
     * @param color      an integer corresponding to a {@link Color}
     * @param brightness the brightness of the LED
     */
    public void setColor(int index, int color, int brightness) {
        if (index < 0 || index >= LEDSTRIP_LENGTH || brightness < 0 || brightness > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        colors[index] = (brightness << 24) | (color & 0xFFFFFF);
    }

    /**
     * Shows the {@code colors} RDB Led data array on the device.
     *
     * @throws IOException
     */
    public void show() throws IOException {
        // Transmit preamble
        for (int i = 0; i < 4; i++) {
            writeByte((byte) 0);
        }

        // Send data
        for (int i = 0; i < LEDSTRIP_LENGTH; i++) {
            int colorIndex = (direction == Direction.NORMAL) ? i : LEDSTRIP_LENGTH - i - 1;
            writeLed(colors[colorIndex]);
        }

        // And latch it
        latch();
    }

    /**
     * Write out a single byte. It goes out MSB first.
     *
     * @param out The byte to write.
     */
    private void writeByte(byte out) throws IOException {
        for (int i = 7; i >= 0; i--) {
            dataGpio.setValue((out & (1 << i)) != 0);
            clockGpio.setValue(true);
            clockGpio.setValue(false);
        }
    }

    /**
     * Write out a single LEDs information.
     *
     * @param data The data for that LED.
     */
    private void writeLed(int data) throws IOException {
        writeByte((byte) (0xE0 | ((data >> 24) & 0x1F))); // Max brightness == 31
        writeByte((byte) (data)); // Blue
        writeByte((byte) (data >> 8)); // Green
        writeByte((byte) (data >> 16)); // Red
    }

    /**
     * Latch the data into the devices. This has prompted much discussion as
     * data sheet seems to be a work of fiction. These values seem to work.
     */
    private void latch() throws IOException {
        // Transmit zeros not ones!
        dataGpio.setValue(false);

        // And 36 of them!
        for (int i = 0; i < 36; i++) {
            clockGpio.setValue(true);
            clockGpio.setValue(false);
        }
    }
}
