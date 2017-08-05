package com.mikehelland.omgbananasremote;

import android.view.View;

import java.util.ArrayList;

public class Jam {
    int subbeats = 4;
    int beats = 8;
    int subbeatLength = 125;
    int currentSubbeat = 0;

    boolean playing = false;
    long timeSinceLast = 0;


    ArrayList<Instrument> instruments = new ArrayList<>();

    private int[] ascale;
    private String scale;
    private int key;

    //TODO what about this? Update this
    private int currentChord = 0;

    ArrayList<View> viewsToInvalidateOnBeat = new ArrayList<>();
    ArrayList<View> viewsToInvalidateOnNewMeasure = new ArrayList<>();


    int getBPM() {
        return 60000 / (subbeatLength * subbeats);
    }
    void setBPM(float bpm) {
        subbeatLength = (int)((60000 / bpm) / subbeats);
        //bpm = 60000 / (subbeatLength * subbeats);
    }
    void setSubbeatLength(int subbeatLength) {
        this.subbeatLength = subbeatLength;
    }
    int getSubbeatLength() {
        return subbeatLength;
    }

    void addInvalidateOnBeatListener(View view) {
        viewsToInvalidateOnBeat.add(view);
    }

    int getTotalSubbeats() {
        return beats * subbeats;
    }

    void setScale(String scale) {
        this.scale = scale;
        String[] splitScale = scale.split(",");
        ascale = new int[splitScale.length];
        for (int i = 0; i < splitScale.length; i++) {
            ascale[i] = Integer.parseInt(splitScale[i]);
        }
    }

    void setKey(int i) {
        key = i;
    }

    int getKey() {
        return key;
    }

    int[] getScale() {
        return ascale;
    }

    String getScaleString() {
        return scale;
    }

    int getScaledNoteNumber(int oldNoteNumber) {
        int newNoteNumber;
        int octaves;

        octaves = 0;

        newNoteNumber = oldNoteNumber + currentChord;

        while (newNoteNumber >= ascale.length) {
            octaves++;
            newNoteNumber = newNoteNumber - ascale.length;
        }

        while (newNoteNumber < 0) {
            octaves--;
            newNoteNumber = newNoteNumber + ascale.length;
        }

        newNoteNumber = ascale[newNoteNumber];


        return key + newNoteNumber + octaves * 12;
    }

    void makeChannel(String channelData) {
        Instrument instrument = new Instrument();
        instrument.channel = instruments.size();
        instrument.name = channelData.substring(1);
        instrument.chromatic = !channelData.startsWith("0");
        if (channelData.startsWith("0"))
            instrument.surfaceType = Instrument.SurfaceType.PRESET_SEQUENCER;
        if (channelData.startsWith("1"))
            instrument.surfaceType = Instrument.SurfaceType.PRESET_VERTICAL;
        if (channelData.startsWith("2"))
            instrument.surfaceType = Instrument.SurfaceType.PRESET_FRETBOARD;

        instruments.add(instrument);
    }

    void makeChannels(String value) {
        instruments.clear();
        String[] channelsData = value.split(",");
        for (String channelData : channelsData) {
            makeChannel(channelData);
        }
    }

    void play() {
        currentSubbeat = 0;
        timeSinceLast = System.currentTimeMillis();
        playing = true;

        invalidateAllViews();
    }

    void stop() {
        playing = false;
    }

    private void invalidateAllViews() {
        for (View v : viewsToInvalidateOnBeat) {
            v.postInvalidate();
        }
        for (View v : viewsToInvalidateOnNewMeasure) {
            v.postInvalidate();
        }
    }
}
