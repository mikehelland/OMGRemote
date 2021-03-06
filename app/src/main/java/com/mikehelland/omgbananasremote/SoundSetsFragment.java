package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SoundSetsFragment extends Fragment {

    BluetoothConnection mConnection;
    String mSoundSetsString;
    Jam mJam;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_jams_fragment,
                container, false);

        ListView list = (ListView)view.findViewById(R.id.saved_list);

        final String[] array = mSoundSetsString.split("\\|");

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, array);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemText = array[i];
                String id = itemText.split(":")[0];

                mConnection.writeString("ADD_CHANNEL=" + id + ";");

                FragmentManager fm = getFragmentManager();
                if (fm != null && fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });

        return view;
    }

}