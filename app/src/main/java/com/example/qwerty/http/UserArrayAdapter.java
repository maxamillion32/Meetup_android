package com.example.qwerty.http;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by qwerty on 10.11.2015.
 */
public class UserArrayAdapter extends ArrayAdapter {
    // application context
    private Context context;
    private ArrayList<JSONObject> users;
    private static String TAG = MeetupArrayAdapter.class.getSimpleName();



    public UserArrayAdapter(Context context, ArrayList<JSONObject> users) {
        super(context, R.layout.user_rowlayout, R.id.textView, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.user_rowlayout, parent, false);
        TextView nameField = (TextView) rowView.findViewById(R.id.usr_txt);


        try {
            nameField.setText(users.get(position).getString("name"));
            JSONObject user = users.get(position).getJSONArray("meetings").getJSONObject(0);

            if(Objects.equals(user.getString("attendance"), "undecided"))
                rowView.setBackgroundColor(Color.YELLOW);
            else if(Objects.equals(user.getString("attendance"), "yes"))
                rowView.setBackgroundColor(Color.GREEN);
            else if(Objects.equals(user.getString("attendance"), "no"))
                rowView.setBackgroundColor(Color.RED);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // return row view
        return rowView;
    }
}
