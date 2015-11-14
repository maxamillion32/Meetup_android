package com.example.qwerty.http;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qwerty on 7.11.2015.
 */
public class MeetupArrayAdapter extends ArrayAdapter{
    // application context
    private Context context;

    private ArrayList<JSONObject> meetups;
    private static String TAG = MeetupArrayAdapter.class.getSimpleName();



    public MeetupArrayAdapter(Context context, ArrayList<JSONObject> meetups) {
        super(context, R.layout.rowlayout, R.id.textView, meetups);
        this.context = context;
        this.meetups = meetups;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    // populate every row in ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView nameField = (TextView) rowView.findViewById(R.id.rowTitleTxt);
        TextView descField = (TextView) rowView.findViewById(R.id.rowDescTxt);
        TextView usrCount = (TextView) rowView.findViewById(R.id.rowUsrCountTxt);
        try {
            JSONObject meetup = meetups.get(position).getJSONObject("meeting");

            nameField.setText(meetup.getString("name"));
            rowView.setTag(meetup.getString("_id"));
            descField.setText(meetup.getString("description"));

            if(meetup.getJSONArray("users").length() == 1)
                usrCount.setText(meetup.getJSONArray("users").length() + " user");
            else
                usrCount.setText(meetup.getJSONArray("users").length() + " users");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return row view
        return rowView;
    }

}