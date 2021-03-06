package com.mikehelland.omgbananasremote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BluetoothConnection extends Thread {
    private BluetoothDevice mDevice;
    private BluetoothManager mBT;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final BluetoothSocket socket;
    private BluetoothConnectCallback mConnectedCallback;

    private List<BluetoothDataCallback> mDataCallbacks = new CopyOnWriteArrayList<>();

    private final static String TAG = "MGH bluetoothconnection";

    private boolean disconnected = false;
    
    public BluetoothConnection(BluetoothDevice device, BluetoothManager bluetoothFactory,
                               BluetoothSocket socket, BluetoothConnectCallback callback){
        this.mBT = bluetoothFactory;
        mConnectedCallback = callback;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.socket = socket;
        mDevice = device;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            bluetoothFactory.newStatus(mConnectedCallback, BluetoothManager.STATUS_IO_OPEN_STREAMS);
            Log.d(TAG, e.getMessage());
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;

    }

    public void run(){

        if (mConnectedCallback != null)
            mConnectedCallback.onConnected(this);

        int bytes;
        boolean hasData;

        while (!isInterrupted()){

            final byte[] buffer = new byte[1024];
            hasData = false;

            try {
                bytes = mmInStream.read(buffer);
                if (bytes > 0) {
                    hasData = true;
                }
                else {
                    Log.d("MGH", "InStream read but zero bytes");
                }
            } catch (IOException e){
                Log.d(TAG, e.getMessage());

                if (!mBT.cleaningUp) {
                    mBT.newStatus(mConnectedCallback, BluetoothManager.STATUS_IO_CONNECTED_THREAD);
                }
                break;
            }

            if (hasData)  {
                String data = new String(buffer, 0, bytes);
                mBT.newData(mDataCallbacks, data);
            }

        }

        disconnected = true;
        if (!mBT.cleaningUp) {
            resetConnections();
        }
    }

    void write(byte[] bytes){
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    void writeString(String toWrite){
        //Log.d("MGH writeString", toWrite);
        try {
            mmOutStream.write(toWrite.getBytes());
        } catch (IOException e) {
            Log.d("MGH BT writeString", e.getMessage());
            if (!mBT.cleaningUp) {
                mBT.newStatus(mConnectedCallback, BluetoothManager.STATUS_IO_CONNECTED_THREAD);
            }
        }
    }


    void resetConnections() {
        try {
            mmOutStream.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            mmInStream.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            socket.close();
            Log.d("MGH", "socket closed");
        }
        catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    boolean isDisconnected() {
        return disconnected;
    }

    void addDataCallback(BluetoothDataCallback callback) {
        mDataCallbacks.add(callback);
    }
    void removeDataCallback(BluetoothDataCallback callback) {
        mDataCallbacks.remove(callback);
    }
}
