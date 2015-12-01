package com.example.qwerty.http;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H3916 on 1.12.2015.
 */
public class DataRetainFragment extends Fragment {

    // data object we want to retain
    private JSONObject data = new JSONObject();
    Activity container;
    EditText title;
    EditText desc;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        container = activity;
        title = (EditText) container.findViewById(R.id.titleTxt);
        desc = (EditText) container.findViewById(R.id.descTxt);
    }


    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(JSONObject dataToSave) {
        try {
            if (dataToSave.has("date")) {
                data.put("date", data.getString("date"));
            }
            if (dataToSave.has("meetup")) {
                data.put("meetup", dataToSave.getJSONObject("meetup"));
            }
            data.put("title", title.getText());
            data.put("description", desc.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("asdasasdasdasdasdasd: ", data.toString());
    }

    public JSONObject getData() {
        return data;
    }

}