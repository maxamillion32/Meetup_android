package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by qwerty on 12.11.2015.
 */
public class MeetupOverviewActivity extends Activity {

    Bundle extras;
    JSONObject meetup = new JSONObject();
    JSONArray users = new JSONArray();
    ListView listview;
    Button attend;
    Button turndown;
    TextView title;
    TextView desc;
    TextView userCount;
    RequestCondenser getDataRequest;
    RequestCondenser editAttendanceRequest;
    JSONObject attendees = new JSONObject();

    ArrayList<JSONObject> userList = new ArrayList<>();

    Context ctx = this;

    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetup_overview);
        attend = (Button) findViewById(R.id.attendBtn);
        turndown = (Button) findViewById(R.id.denyBtn);
        title = (TextView) findViewById(R.id.meetupTitle);
        desc = (TextView) findViewById(R.id.meetupDesc);
        userCount = (TextView) findViewById(R.id.usrCountTxt);
        listview = (ListView) findViewById(R.id.usrList);

        extras = getIntent().getExtras();

        getDataRequest = new RequestCondenser(
                Request.Method.POST,
                populatedGetRequestBody(),
                getString(R.string.apiUrl).concat("/meetup/get"),
                TAG,
                ctx
        );
        editAttendanceRequest = new RequestCondenser(
                Request.Method.POST,
                getString(R.string.apiUrl).concat("/user/attendance"),
                TAG,
                ctx
        );
        getDataRequest.request(new RequestCondenser.ActionOnResponse() {
            @Override
            public void responseCallBack(JSONObject response) {
                try {
                    title.setText(response.getString("name"));
                    desc.setText(response.getString("description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                generateUserList(response);
                displayAttendees();
            }
        });

        turndown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAttendanceRequest.setRequestBody(attendanceEditRequestBody(false));
                editAttendanceRequest.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
                        generateUserList(response);
                        displayAttendees();
                    }
                });
            }
        });

        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAttendanceRequest.setRequestBody(attendanceEditRequestBody(true));
                editAttendanceRequest.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
                        generateUserList(response);
                        displayAttendees();
                    }
                });
            }
        });

    }
    private JSONObject populatedGetRequestBody() {
        try {
            meetup.put("_id", extras.getString("meetup"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meetup;
    }

    private JSONObject attendanceEditRequestBody(boolean yesOrNo) {
        try {
            meetup.put("meetup", extras.getString("meetup"));
            meetup.put("_id", extras.getString("uid"));
            if(yesOrNo)
                meetup.put("attendance", "yes");
            else
                meetup.put("attendance", "no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meetup;
    }



    private void generateUserList(JSONObject response) {
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
                                    .getString("attendance")
                        , "yes")
                    )
                        attendees.put("yesmen", attendees.getInt("yesmen")+1);
                    userList.add(users.getJSONObject(i));
                    attendees.put("total", attendees.getInt("total")+1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // add data to ArrayAdapter
        UserArrayAdapter adapter = new UserArrayAdapter(ctx, userList);
        // set data to listView with adapter
        listview.setAdapter(adapter);

    }

    private void displayAttendees() {
        try {
            userCount
                    .setText(
                            attendees.getInt("yesmen") + "/" +
                                    attendees.getInt("total") +
                                    " Users attending"
                );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
