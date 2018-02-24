package com.mikehelland.omgbananasremote;

class RemoteControlBluetoothHelper {
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

    static void playNote(BluetoothConnection connection, Note note) {
        int instrumentNumber = note.isRest() ? -1 : note.getInstrumentNote();
        int basicNote = note.isRest() ? -1 : note.getBasicNote();
        connection.writeString("CHANNEL_PLAY_NOTE=" + basicNote + "," + instrumentNumber + ";");

    }

    static void getJamInfo(BluetoothConnection connection) {
        connection.writeString("GET_JAM_INFO=TRUE;");

    }

    static void setPlay(BluetoothConnection connection) {
        connection.writeString("SET_PLAY;");
    }
    static void setStop(BluetoothConnection connection) {
        connection.writeString("SET_STOP;");
    }

    static void getSavedJams(BluetoothConnection connection) {
        connection.writeString("GET_SAVED_JAMS=TRUE;");
    }
    static void getSoundSets(BluetoothConnection connection) {
        connection.writeString("GET_SOUNDSETS=TRUE;");
    }

    static void setArpeggiator(BluetoothConnection connection, int arpeggiate) {
        connection.writeString("SET_ARPEGGIATOR=" + Integer.toString(arpeggiate) + ";");
    }
    static void setArpNotes(BluetoothConnection connection, Note[] notes) {
        String output = "";
        for (int i = 0; i < notes.length; i++) {
            output += notes[i].getBasicNote() + "," + notes[i].getInstrumentNote();
            if (i < notes.length - 1) {
                output += "|";
            }
        }
        connection.writeString("SET_ARPNOTES=" + output + ";");
    }

    static void clearChannel(BluetoothConnection connection, int channel) {
        connection.writeString("CLEAR_CHANNEL=" + channel + ";");
    }
}
