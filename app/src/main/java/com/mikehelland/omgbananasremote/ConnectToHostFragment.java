package com.mikehelland.omgbananasremote;


import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ConnectToHostFragment extends Fragment {

    private BluetoothConnection mConnection;
    private View mView;
    private TextView mStatusText;
    private ImageView mImageView;
    private BluetoothManager mBT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bluetooth_remote,
                container, false);

        final MainActivity activity = (MainActivity)getActivity();
        mBT = activity.mBT;

        mStatusText = (TextView)mView.findViewById(R.id.bt_status);
        mImageView = (ImageView)mView.findViewById(R.id.remote_logo);

        if (mConnection != null && !mConnection.isDisconnected()) {
            mStatusText.setText(R.string.connected);
            mImageView.setImageResource(R.drawable.device_blue);
            showRemoteControlFragmentAfterDelay(500);
        }
        else {
            mBT.whenReady(new BluetoothReadyCallback() {
                @Override
                public void onReady() {
                    setup();
                }
            });
        }

        return mView;
    }

    private void setup() {
        mStatusText.setText(R.string.looking_for_default_host);

        Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        turnin.setDuration(4000);
        turnin.setRepeatCount(100);
        mImageView.startAnimation(turnin);

        Button connectButton = (Button)mView.findViewById(R.id.choose_host_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseHost();
            }
        });

        final Host host = new Host();
        host.address = PreferenceManager.
                getDefaultSharedPreferences(getContext()).getString("default_host", "");
        host.name = PreferenceManager.
                getDefaultSharedPreferences(getContext()).getString("default_host_name", "");

        if (host.address.length() == 0) {
            chooseHost();
        }
        else {
            connectToHost(host);
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToHost(host);
            }
        });
    }

    private void connectToHost(Host host) {

        BluetoothDevice device = null;
        for (BluetoothDevice bd : mBT.getPairedDevices()) {
            if (bd.getAddress().equals(host.address)) {
                device = bd;
            }
        }

        if (device == null) {
            mStatusText.setText(String.format(getString(R.string.device_not_paired), host.name));
            return;
        }

        final Jam jam = ((MainActivity)getActivity()).mJam;

        mBT.connectTo(device, new BluetoothConnectCallback() {
            @Override
            public void newStatus(final String status) {
                if (getActivity() == null)
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status.equals(BluetoothManager.STATUS_IO_CONNECTED_THREAD)) {
                            mImageView.setImageResource(R.drawable.device);
                            mStatusText.setText(R.string.accepting_connections);
                            int stackCount = getFragmentManager().getBackStackEntryCount();
                            for (int i = 0; i < stackCount; i++) {
                                getFragmentManager().popBackStack();
                            }
                        }
                        else {
                            mStatusText.setText(status);
                        }
                    }
                });
            }

            @Override
            public void onConnected(BluetoothConnection connection) {
                mConnection = connection;
                mConnection.addDataCallback(new CoreBluetoothDataCallback(jam));
                RemoteControlBluetoothHelper.getJamInfo(mConnection);

                if (getActivity() == null)
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatusText.setText(R.string.connected);
                        mImageView.setImageResource(R.drawable.device_blue);
                        showRemoteControlFragment();
                    }
                });
            }
        });
    }

    private void showRemoteControlFragment() {
        RemoteControlFragment f = new RemoteControlFragment();
        f.mConnection = mConnection;
        f.mJam = ((MainActivity)getActivity()).mJam;
        showFragment(f);
    }

    private void showFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_out_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down
        );
        ft.replace(R.id.main_layout, f);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void chooseHost() {
        BluetoothChooseDeviceFragment f = new BluetoothChooseDeviceFragment();
        f.setCallback(new BluetoothChooseDeviceFragment.Callback() {
            @Override
            void run(BluetoothDevice device) {
                Host host = new Host();
                host.address = device.getAddress();
                host.name = device.getName();

                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(getContext()).edit();
                editor.putString("default_host", host.address);
                editor.putString("default_host_name", host.name);
                editor.commit();
            }
        });
        showFragment(f);
    }

    private void showRemoteControlFragmentAfterDelay(final int delay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showRemoteControlFragment();
            }
        }).start();
    }
}