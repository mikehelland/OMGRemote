package com.mikehelland.omgbananasremote;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by m on 7/31/16.
 */
public class Jam {
    int subbeats = 4;
    int beats = 8;
    int totalsubbeats = subbeats * beats;
    int subbeatLength = 125; //70 + rand.nextInt(125); // 125;
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


    public int getBPM() {
        return 60000 / (subbeatLength * subbeats);
    }
    public void setBPM(float bpm) {
        subbeatLength = (int)((60000 / bpm) / subbeats);
        //bpm = 60000 / (subbeatLength * subbeats);
    }
    public void setSubbeatLength(int subbeatLength) {
        this.subbeatLength = subbeatLength;
    }
    public int getSubbeatLength() {
        return subbeatLength;
    }

    public void addInvalidateOnBeatListener(View view) {
        viewsToInvalidateOnBeat.add(view);
    }

    public int getTotalSubbeats() {
        return beats * subbeats;
    }

    public void setScale(String scale) {
        this.scale = scale;
        String[] splitScale = scale.split(",");
        ascale = new int[splitScale.length];
        for (int i = 0; i < splitScale.length; i++) {
            ascale[i] = Integer.parseInt(splitScale[i]);
        }
    }

    public void setKey(int i) {
        key = i;
    }

    public int getKey() {
        return key;
    }

    public int[] getScale() {
        return ascale;
    }

    public String getScaleString() {
        return scale;
    }

    public int getScaledNoteNumber(int oldNoteNumber) {
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

    public void makeChannels(String value) {

        int i = 0;
        instruments.clear();
        Instrument instrument;
        String[] channelsData = value.split(",");
        for (String channelData : channelsData) {
            instrument = new Instrument();
            instrument.channel = i++;
            instrument.name = channelData.substring(1);
            instrument.chromatic = !channelData.startsWith("0");
            instruments.add(instrument);
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

    void invalidateAllViews() {
        for (View v : viewsToInvalidateOnBeat) {
            v.postInvalidate();
        }
        for (View v : viewsToInvalidateOnNewMeasure) {
            v.postInvalidate();
        }
    }
}
