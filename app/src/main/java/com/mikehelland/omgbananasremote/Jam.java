package com.mikehelland.omgbananasremote;

import android.view.View;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Jam {
    static String[] KEY_CAPTIONS = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};
    static String[] SCALE_CAPTIONS = {"Major", "Minor", "Pentatonic", "Blues"};
    static String[] SCALES = {"0,2,4,5,7,9,11", "0,2,3,5,7,8,10", "0,2,4,7,9", "0,3,5,6,7,10"};

    int subbeats = 4;
    int beats = 8;
    int subbeatLength = 125;
    int currentSubbeat = 0;

    boolean playing = false;
    long timeSinceLast = 0;


    final List<Instrument> instruments = new CopyOnWriteArrayList<>();

    private int[] ascale;
    private int scaleI;
    private String scale;
    private int key;

    //TODO what about this? Update this
    private int currentChord = 0;

    List<View> viewsToInvalidateOnBeat = new CopyOnWriteArrayList<>();
    List<View> viewsToInvalidateOnNewMeasure = new CopyOnWriteArrayList<>();

    PlaybackThread mPlaybackThread = new PlaybackThread(this);

    Jam() {
        mPlaybackThread.start();
    }

    String getKeyName() {
        return KEY_CAPTIONS[key] + " " + SCALE_CAPTIONS[scaleI];
    }

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
        for (int i = 0; i < SCALES.length; i++) {
            if (SCALES[i].equals(scale)) {
                scaleI = i;
                break;
            }
        }

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
        String[] dataParts = channelData.split(",");

        Instrument instrument = new Instrument();
        instrument.channelNumber = instruments.size();
        instrument.name = dataParts[4];
        instrument.enabled = !dataParts[0].equals("0");
        instrument.chromatic = !dataParts[1].equals("0");
        String surfaceType = dataParts[2];
        if (surfaceType.startsWith("0"))
            instrument.surfaceType = Instrument.SurfaceType.PRESET_SEQUENCER;
        if (surfaceType.startsWith("1"))
            instrument.surfaceType = Instrument.SurfaceType.PRESET_VERTICAL;
        if (surfaceType.startsWith("2"))
            instrument.surfaceType = Instrument.SurfaceType.PRESET_FRETBOARD;

        instrument.volume = Float.parseFloat(dataParts[3]);
        synchronized (instruments) {
            instruments.add(instrument);
        }
    }

    void makeChannels(String value) {

        synchronized (instruments) {
            instruments.clear();

            if (value.length() == 0) return;

            String[] channelsData = value.split("\\|");
            for (String channelData : channelsData) {
                makeChannel(channelData);
            }
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

    boolean isPlaying() {
        return playing;
    }

    int getTotalBeats() {
        return beats;
    }

    int getSubbeats() {
        return subbeats;
    }

    int getCurrentSubbeat() {
        return currentSubbeat;
    }

}
