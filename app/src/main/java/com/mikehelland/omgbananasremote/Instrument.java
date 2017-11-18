package com.mikehelland.omgbananasremote;

class Instrument {
    int channel;
    String name;
    float volume;

    SurfaceType surfaceType;
    boolean defaultSurface = true;
    boolean chromatic;

    enum SurfaceType {
        PRESET_SEQUENCER, PRESET_VERTICAL, PRESET_FRETBOARD
    }
}
