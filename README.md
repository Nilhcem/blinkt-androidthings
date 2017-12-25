# Pimoroni Blinkt driver for Android Things

An Android Things port of the Blink driver from [hackerjimbo][hackerjimbo].
Tested on the Raspberry Pi 3.
Untested (yet) on NXP boards.


## Photo

![photo]


## Download

```groovy
dependencies {
    implementation 'com.nilhcem.androidthings:driver-blinkt:0.0.1'
}
```


## Usage

```java
Blinkt blinkt = new Blinkt(); // or new Blinkt(DATA_GPIO, CLOCK_GPIO);

// TODO

// Later on
blinkt.close();
```

[hackerjimbo]: https://github.com/hackerjimbo/PiJava/
[photo]: https://raw.githubusercontent.com/Nilhcem/blinkt-androidthings/master/assets/photo.jpeg
