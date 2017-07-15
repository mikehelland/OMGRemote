package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by m on 7/30/16.
 */
public class BPMFragment extends Fragment {

    Jam mJam;
    //private MainFragment mMainFragment;

    private View mView;
    private TextView bpmText;
    private VerticalSliderView bpmSeekBar;

    private TextView volText;
    private VerticalSliderView volSeekBar;

    BluetoothConnection mConnection;

    private int bpm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bpm_fragment,
                container, false);

        mView = view;
        bpmText = (TextView)view.findViewById(R.id.bpm_caption);
        bpmSeekBar = (VerticalSliderView) view.findViewById(R.id.bpm_seekbar);

        volText = (TextView)view.findViewById(R.id.master_volume_caption);
        volSeekBar = (VerticalSliderView) view.findViewById(R.id.master_volume_seekbar);

        //bpmSeekBar.setMax(200);

        //if (mJam != null)
            setup();

        return view;
    }

    /*public void setJam(Jam jam, MainFragment mainFragment) {
        mJam = jam;
        mMainFragment = mainFragment;

        if (mView != null)
            setup();
    }*/

    private void setup() {

        bpm = mJam.getBPM();
        bpmSeekBar.value = (bpm - 20) / 200.0f;
        bpmText.setText(Integer.toString(bpm));

        bpmSeekBar.onValueChanged = new VerticalSliderView.OnValueChangedListener() {
            @Override
            void onValueChanged(float f) {
                Log.d("MGH new seek value", Float.toString(f));
                int newBPM = Math.round(f * 200 + 20);
                bpmText.setText(Integer.toString(newBPM));
                mJam.setBPM((float)newBPM);
                RemoteControlBluetoothHelper.sendNewSubbeatLength(mConnection, mJam.getSubbeatLength());
            }
        };
        volSeekBar.onValueChanged = new VerticalSliderView.OnValueChangedListener() {
            @Override
            void onValueChanged(float f) {
                int newBPM = Math.round(f * 100);
                volText.setText(Integer.toString(newBPM) + "%");
            }
        };

    }


}
