package com.mikehelland.omgbananasremote;

public abstract class BluetoothConnectCallback {

    public abstract void newStatus(String status);
    public abstract void onConnected(BluetoothConnection connection);


}
