package com.mikehelland.omgbananasremote;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class AcceptConnectionFragment extends Fragment {

    private BluetoothConnection mConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_remote,
                container, false);

        //getActivityMembers();
        final MainActivity activity = (MainActivity)getActivity();
        BluetoothFactory btf = null;//activity.mBtf;

        final TextView statusView = (TextView)view.findViewById(R.id.bt_status);

        final ImageView spinningImage = (ImageView)view.findViewById(R.id.remote_logo);
        Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        turnin.setDuration(4000);
        turnin.setRepeatCount(100);
        spinningImage.startAnimation(turnin);

        spinningImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoteControlFragment();
            }
        });
        Log.d("MGH", "Accept Connection Fragment onCreate View");
        btf.startAccepting(new BluetoothConnectCallback() {
            @Override
            public void newStatus(final String status) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status.equals(BluetoothFactory.STATUS_IO_CONNECTED_THREAD)) {
                            spinningImage.setImageResource(R.drawable.device);
                            statusView.setText(R.string.accepting_connections);
                            int stackCount = getFragmentManager().getBackStackEntryCount();
                            for (int i = 0; i < stackCount; i++) {
                                getFragmentManager().popBackStack();
                            }
                        }
                        else {
                            statusView.setText(status);
                        }
                    }
                });
            }


            @Override
            public void onConnected(BluetoothConnection connection) {
                mConnection = connection;
                Log.d("MGH", "Accept Connection Fragment onConnected");

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setText(R.string.connected);
                        spinningImage.setImageResource(R.drawable.device_blue);
                        showRemoteControlFragment();
                    }
                });

            }
        });


        return view;
    }


    private void showRemoteControlFragment() {
        RemoteControlFragment f = new RemoteControlFragment();
        f.mConnection = mConnection;
        f.mJam = new Jam();
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

}