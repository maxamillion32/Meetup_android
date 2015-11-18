package com.example.qwerty.http;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qwerty on 18.11.2015.
 */

public class UserListFragment extends ListFragment {
    JSONArray users = new JSONArray();
    ArrayList<JSONObject> userList = new ArrayList<>();
    Activity container;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        container = activity;
    }

    private void displayAttendees() {
        try {
            container.findViewById(getActivity().)
                    .setText(
                            attendees.getInt("yesmen") + "/" +
                                    attendees.getInt("total") +
                                    " Users attending"
                    );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void passDataToFragment(JSONObject response) {
        try {
                if (!response.isNull("users")) {

                    users = response.getJSONArray("users");
                    userList.clear();

                    for (int i = 0; i < users.length(); ++i) {
                        try {
                            userList.add(users.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // add data to ArrayAdapter
                    UserArrayAdapter adapter = new UserArrayAdapter(getActivity(), userList);
                    // set data to listView with adapter
                    setListAdapter(adapter);
                } else
                    Toast.makeText(getActivity(),
                            "No such user exists!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_list, container, false);
    }
}
