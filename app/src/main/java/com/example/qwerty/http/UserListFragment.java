package com.example.qwerty.http;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by qwerty on 18.11.2015.
 */

public class UserListFragment extends ListFragment {
    JSONArray users = new JSONArray();
    ArrayList<JSONObject> userList = new ArrayList<>();
    Activity container;
    TextView userCounter;
    JSONObject attendees = new JSONObject();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        container = activity;
    }

    public void displayAttendees() {
        try {
            userCounter = (TextView) container.findViewById(R.id.usrCountTxt);
            userCounter.setText(
                    String.format("%d/%d Users attending",
                            attendees.getInt("yesmen"),
                            attendees.getInt("total")
                    )
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void passDataToFragment(JSONObject response) {
        try {
            users = response.getJSONArray("users");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        userList.clear();
        try {
            attendees.put("yesmen", 0);
            attendees.put("total", 0);
            for (int i = 0; i < users.length(); ++i) {

                if(
                    Objects.equals(
                        users.getJSONObject(i)
                            .getJSONArray("meetings")
                                .getJSONObject(0)
                                    .getString("attendance"), "yes"
                    )
                )
                    attendees.put("yesmen", attendees.getInt("yesmen")+1);

                userList.add(users.getJSONObject(i));
                attendees.put("total", attendees.getInt("total")+1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserArrayAdapter adapter = new UserArrayAdapter(getActivity(), userList);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_list, container, false);
    }
}
