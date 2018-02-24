package com.mikehelland.omgbananasremote;

import java.util.ArrayList;

class Channel {

    BluetoothConnection mConnection;

    int lowNote;
    int highNote;
    int octave;

    ArrayList<Note> noteList = new ArrayList<>();

    String name;
    boolean chromatic;

    private int arpeggiate = -1;

    Channel(BluetoothConnection connection) {
        mConnection = connection;
    }

    void playLiveNote(Note note, boolean multitrack) {
        playLiveNote(note);
    }
    void playLiveNote(Note note) {
        if (note.isRest()) {
            arpeggiate = 0;
        }
        RemoteControlBluetoothHelper.playNote(mConnection, note);
    }

    int getOctave() {
        return octave;
    }
    int getLowNote() {
        return lowNote;
    }
    int getHighNote() {
        return highNote;
    }

    String[] soundsetCaptions =  new String[0];

    int getInstrumentNoteNumber(int scaledNote) {
        int noteToPlay = scaledNote + octave * 12;

        while (noteToPlay < lowNote) {
            noteToPlay += 12;
        }
        while (noteToPlay > highNote) {
            noteToPlay -= 12;
        }

        noteToPlay -= lowNote;

        return noteToPlay;
    }

    void setArpeggiator(int i) {
        if (arpeggiate != i) {
            arpeggiate = i;
            RemoteControlBluetoothHelper.setArpeggiator(mConnection, arpeggiate);
        }
    }

    ArrayList<Note> getNotes() {
        return noteList;
    }
}
