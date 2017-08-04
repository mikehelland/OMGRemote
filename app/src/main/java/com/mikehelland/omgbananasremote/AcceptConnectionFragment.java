package com.mikehelland.omgbananasremote;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
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
    private View mView;
    private BluetoothFactory mBtf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bluetooth_remote,
                container, false);

        //getActivityMembers();
        final MainActivity activity = (MainActivity)getActivity();
        mBtf = activity.mBtf;

        final TextView statusView = (TextView)mView.findViewById(R.id.bt_status);

        final ImageView spinningImage = (ImageView)mView.findViewById(R.id.remote_logo);
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
        mBtf.startAccepting(new BluetoothConnectCallback() {
            @Override
            public void newStatus(final String status) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status.equals(BluetoothFactory.STATUS_IO_CONNECTED_THREAD)) {
                            spinningImage.setImageResource(R.drawable.device);
                            statusView.setText("Accepting Connections...");
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
                        statusView.setText("Connected");
                        spinningImage.setImageResource(R.drawable.device_blue);
                        showRemoteControlFragment();
                    }
                });

            }
        });


        return mView;
    }


    private void showRemoteControlFragment() {
        RemoteControlFragment f = new RemoteControlFragment();
        f.mConnection = mConnection;
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