package com.mikehelland.omgbananasremote;

/**
 * Created by m on 7/30/16.
 */
public class Instrument {
    int channel;
    String name;
    float volume;

    SurfaceType surfaceType;
    boolean chromatic;

    enum SurfaceType {
        PRESET_SEQUENCER, PRESET_VERTICAL, PRESET_FRETBOARD
    }
}
