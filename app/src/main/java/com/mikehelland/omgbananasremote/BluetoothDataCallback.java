package com.mikehelland.omgbananasremote;

abstract class BluetoothDataCallback {

    abstract void newData(String name, String value);
    boolean finished = false;

}
