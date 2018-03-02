package com.mikehelland.omgbananasremote;

class Instrument {
    int channelNumber;
    String name;
    float volume;
    float pan = 0;
    boolean enabled = true;

    SurfaceType surfaceType;
    boolean defaultSurface = true;
    boolean chromatic;

    enum SurfaceType {
        PRESET_SEQUENCER, PRESET_VERTICAL, PRESET_FRETBOARD
    }

    boolean isEnabled() { return enabled; }
    boolean toggleEnabled() {
        enabled = !enabled;
        return enabled;
    }
}
