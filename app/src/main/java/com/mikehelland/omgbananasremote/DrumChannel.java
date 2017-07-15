package com.mikehelland.omgbananasremote;

public class DrumChannel {

    private BluetoothConnection mConnection;
    boolean enabled = false;

    String[] captions = {"1", "2", "3", "4", "5", "6", "7", "8"};
    boolean[][] pattern = new boolean[8][32];

    public DrumChannel(BluetoothConnection connection) {

        mConnection = connection;

    }

    public void setPattern(int track, int subbeat, boolean value) {
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

}
