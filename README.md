# Pimoroni Blinkt! driver for Android Things

_Tested successfully on the Raspberry Pi 3, and the Pico i.MX7D._


## Photo

![photo]


## Download

```groovy
dependencies {
    implementation 'com.nilhcem.androidthings:driver-blinkt:0.0.3'
}
```


## Usage

### Easy (global brightness, sending an array of colors)
```java
Blinkt blinkt = new Blinkt();
blinkt.setBrightness(1);
blinkt.write(new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.DKGRAY, Color.YELLOW, Color.MAGENTA, Color.CYAN, 0xFF9B6E26});
blinkt.close();
```

### Advanced (brightness per LED, sending colors individually, showing in a reversed order)
```java
Blinkt blinkt = new Blinkt(DATA_GPIO, CLOCK_GPIO);
blinkt.setDirection(Blinkt.Direction.REVERSED);

for (int i = 0; i < Blinkt.LEDSTRIP_LENGTH; i++) {
    int color = Color.HSVToColor(255, new float[]{i * 360.f / Blinkt.LEDSTRIP_LENGTH, 1.0f, 1.0f});
    int brightness = (int) ((float) Blinkt.MAX_BRIGHTNESS / Blinkt.LEDSTRIP_LENGTH * (i + 1));
    blinkt.setColor(i, color, brightness);
}
blinkt.show();

blinkt.close();
```


## Kudos

This project was inspired by code from:
* Android Things's official [APA102 driver][apa102]
* hackerjimbo's [Blinkt! Pi4J port][hackerjimbo]


[apa102]: https://github.com/androidthings/contrib-drivers/tree/master/apa102
[hackerjimbo]: https://github.com/hackerjimbo/PiJava/
[photo]: https://raw.githubusercontent.com/Nilhcem/blinkt-androidthings/master/assets/photo.jpg
