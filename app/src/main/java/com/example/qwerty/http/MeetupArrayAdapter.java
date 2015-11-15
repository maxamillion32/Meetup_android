package com.example.qwerty.http;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by qwerty on 7.11.2015.
 */
public class MeetupArrayAdapter extends ArrayAdapter{
    // application context
    private Context context;
    private String uid;
    private RequestCondenser editAttendanceRequest;
    private JSONObject body = new JSONObject();

    private ArrayList<JSONObject> meetups;
    private static String TAG = MeetupArrayAdapter.class.getSimpleName();



    public MeetupArrayAdapter(Context context, ArrayList<JSONObject> meetups, String uid, String apiUrl) {
        super(context, R.layout.rowlayout, R.id.textView, meetups);
        this.context = context;
        this.meetups = meetups;
        this.uid = uid;
        editAttendanceRequest = new RequestCondenser(
                                    Request.Method.POST,
                                    apiUrl,
                                    TAG,
                                    context
                                );
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    // populate every row in ListView
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView nameField = (TextView) rowView.findViewById(R.id.rowTitleTxt);
        TextView descField = (TextView) rowView.findViewById(R.id.rowDescTxt);
        TextView usrCount = (TextView) rowView.findViewById(R.id.rowUsrCountTxt);
        final CheckBox attendance = (CheckBox) rowView.findViewById(R.id.checkBox);
        try {
            final JSONObject meetup = meetups.get(position).getJSONObject("meeting");

            nameField.setText(meetup.getString("name"));
            rowView.setTag(meetup.getString("_id"));
            descField.setText(meetup.getString("description"));

            attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!attendance.isChecked())
                        editAttendanceRequest.setRequestBody(attendanceEditRequestBody(false, meetup));
                    else
                        editAttendanceRequest.setRequestBody(attendanceEditRequestBody(true, meetup));
                    editAttendanceRequest.request(new RequestCondenser.ActionOnResponse() {
                        @Override
                        public void responseCallBack(JSONObject response) {
                            try {
                                Toast.makeText(context,
                                    "Your attendance status on " +
                                    meetup.getString("name") +
                                    " has been changed to '" +
                                    body.getString("attendance") +
                                    "'", Toast.LENGTH_LONG
                                ).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            if(Objects.equals(meetups.get(position).getString("attendance"), "yes"))
                attendance.setChecked(true);
            else
                attendance.setChecked(false);
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

    private JSONObject attendanceEditRequestBody(boolean yesOrNo, JSONObject meetup) {
        try {
            body.put("meetup", meetup.getString("_id"));
            body.put("_id", uid);
            if(yesOrNo)
                body.put("attendance", "yes");
            else
                body.put("attendance", "no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;
    }

}