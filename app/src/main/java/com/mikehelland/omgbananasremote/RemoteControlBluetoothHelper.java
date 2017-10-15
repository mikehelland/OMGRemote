package com.mikehelland.omgbananasremote;

/**
 * Created by m on 7/31/16.
 */
public class RemoteControlBluetoothHelper {
    static void sendNewSubbeatLength(BluetoothConnection connection, int subbeatLength) {
        connection.writeString("SET_SUBBEATLENGTH=" + subbeatLength + ";");
    }

    static void setChannel(BluetoothConnection connection, int channel) {
        connection.writeString("SET_CHANNEL=" + channel + ";");
    }

    static void setChord(BluetoothConnection connection, int value) {
        connection.writeString("SET_CHORD=" + value + ";");
    }
    static void setKey(BluetoothConnection connection, int value) {
        connection.writeString("SET_KEY=" + value + ";");
    }
    static void setScale(BluetoothConnection connection, String value) {
        connection.writeString("SET_SCALE=" + value + ";");
    }

    public static void playNote(BluetoothConnection connection, Note note) {
        int instrumentNumber = note.isRest() ? -1 : note.getInstrumentNote();
        int basicNote = note.isRest() ? -1 : note.getBasicNote();
        connection.writeString("CHANNEL_PLAY_NOTE=" + basicNote + "," + instrumentNumber + ";");

    }

    public static void getJamInfo(BluetoothConnection connection) {
        connection.writeString("GET_JAM_INFO=TRUE;");

    }
}
