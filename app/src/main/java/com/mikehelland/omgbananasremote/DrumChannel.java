package com.mikehelland.omgbananasremote;

class DrumChannel {

    private BluetoothConnection mConnection;
    private Instrument mInstrument;

    String[] captions = {"1", "2", "3", "4", "5", "6", "7", "8"};
    boolean[][] pattern = new boolean[8][32];

    String name = "";

    DrumChannel(BluetoothConnection connection, Instrument instrument) {
        mConnection = connection;
        mInstrument = instrument;
        if (mInstrument != null) {
            name = mInstrument.name;
        }
    }

    void setPattern(int track, int subbeat, boolean value) {
        pattern[track][subbeat] = value; //super.setPattern(track, subbeat, value);

        mConnection.writeString("CHANNEL_SET_PATTERN=" + track + "," + subbeat + "," + value + ";");
    }

    String[] getCaptions() {
        return captions;
    }

    boolean[] getTrack(int track) {
        //return data[track];
        return pattern[track];
    }

    boolean isEnabled() {
        return mInstrument != null && mInstrument.isEnabled();
    }
}
