package com.mikehelland.omgbananasremote;

import java.util.ArrayList;

/**
 * Created by m on 7/31/16.
 */
public class Channel {

    BluetoothConnection mConnection;

    int lowNote;
    int highNote;
    int octave;

    ArrayList<Note> noteList = new ArrayList<>();

    String name;
    boolean chromatic;

    public Channel(BluetoothConnection connection) {
        mConnection = connection;
    }

    public void playLiveNote(Note note, boolean multitrack) {
        playLiveNote(note);
    }
    public void playLiveNote(Note note) {
        RemoteControlBluetoothHelper.playNote(mConnection, note);
    }

    public int getOctave() {
        return octave;
    }
    public int getLowNote() {
        return lowNote;
    }
    public int getHighNote() {
        return highNote;
    }

    public int getInstrumentNoteNumber(int scaledNote) {
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

}
