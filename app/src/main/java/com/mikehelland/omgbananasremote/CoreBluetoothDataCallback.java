package com.mikehelland.omgbananasremote;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

class CoreBluetoothDataCallback extends BluetoothDataCallback {

    Jam mJam;
    CoreBluetoothDataCallback(Jam jam) {
        mJam = jam;
    }

    public void newData(String name, String value) {
        Log.d("MGH bt newData", name + (value != null ? value : ""));

        if ("JAMINFO_SUBBEATLENGTH".equals(name)) {
            mJam.setSubbeatLength(Integer.parseInt(value));
            return;
        }

        if ("PLAY".equals(name)) {
            mJam.play();
            return;
        }

        if ("STOP".equals(name)) {
            mJam.stop();
            return;
        }

        if ("JAMINFO_KEY".equals(name)) {
            mJam.setKey(Integer.parseInt(value));
            return;
        }

        if ("JAMINFO_SCALE".equals(name)) {
            mJam.setScale(value);
        }

        if ("JAMINFO_CHANNELS".equals(name)) {
            mJam.makeChannels(value);
        }

        if ("NEW_CHANNEL".equals(name)) {
            mJam.makeChannel(value);
        }

        if ("CHANNEL_ENABLED".equals(name)) {
            try {
                String[] data = value.split(",");
                mJam.instruments.get(Integer.parseInt(data[1])).enabled = !"0".equals(data[0]);
            }
            catch (Exception ignored) {}
        }
        if ("CHANNEL_VOLUME".equals(name)) {
            try {
                String[] data = value.split(",");
                mJam.instruments.get(Integer.parseInt(data[1])).volume = Float.parseFloat(data[0]);
            }
            catch (Exception ignored) {}
        }
        if ("CHANNEL_PAN".equals(name)) {
            try {
                String[] data = value.split(",");
                mJam.instruments.get(Integer.parseInt(data[1])).pan = Float.parseFloat(data[0]);
            }
            catch (Exception ignored) {}
        }

        if ("LAUNCH_FRETBOARD".equals(name)) {
            String[] lowhigh = value.split(",");
            //launchFretboard(Integer.parseInt(lowhigh[0]), Integer.parseInt(lowhigh[1]),
            //        Integer.parseInt(lowhigh[2]));

        }
        else if ("LAUNCH_DRUMPAD".equals(name)) {
            boolean[][] pattern;
            try {
                Log.d("MGH launch drumpad", value.substring(value.length() - 20));
                JSONArray jsonPattern = new JSONArray(value);
                JSONArray jsonTrackPattern;
                pattern = new boolean[jsonPattern.length()][];
                for (int i = 0; i < jsonPattern.length(); i++) {
                    jsonTrackPattern = jsonPattern.getJSONArray(i);
                    pattern[i] = new boolean[jsonTrackPattern.length()];
                    for (int j = 0; j < jsonTrackPattern.length(); j++) {
                        pattern[i][j] = jsonTrackPattern.getBoolean(j);
                    }
                }

                //launchDrumpad(pattern);

            }
            catch (JSONException ex) {

                Log.d("MGH launch drumpad", "json exception");

            }

        }

    }

    /*void setJamInfo(String value) {
        try {
            JSONObject jamInfo = new JSONObject(value);
            mJam.setSubbeatLength(jamInfo.getInt("subbeatLength"));
            mJam.setKey(jamInfo.getInt("key"));

            mJam.setScale(jamInfo.getString("scale"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
