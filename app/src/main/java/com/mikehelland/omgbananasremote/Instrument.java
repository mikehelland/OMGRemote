package com.mikehelland.omgbananasremote;

/**
 * Created by m on 7/30/16.
 */
public class Instrument {
    int channel;
    String name;

    SurfaceType type;
    boolean chromatic;

    enum SurfaceType {
        DRUM_MACHINE, FRETBOARD, FULL_RANGE
    }
}
