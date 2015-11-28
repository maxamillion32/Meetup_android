package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by qwerty on 12.11.2015.
 */
public class MeetupOverviewActivity extends Activity {

    Bundle extras;
    JSONObject meetup = new JSONObject();

    Button attend;
    Button turndown;
    TextView title;
    TextView desc;
    TextView date;
    TextView userCount;
    RequestCondenser getDataRequest;
    RequestCondenser editAttendanceRequest;
    UserListFragment usersFragment;
    DateParser dateParser = new DateParser();

    Context ctx = this;

    private static String TAG = MeetupOverviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetup_overview);
        attend = (Button) findViewById(R.id.attendBtn);
        turndown = (Button) findViewById(R.id.denyBtn);
        title = (TextView) findViewById(R.id.meetupTitle);
        desc = (TextView) findViewById(R.id.meetupDesc);
        date = (TextView) findViewById(R.id.dateTxt);
        userCount = (TextView) findViewById(R.id.usrCountTxt);
        usersFragment = (UserListFragment) getFragmentManager().findFragmentById(R.id.list);

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
                    Log.d(TAG, String.valueOf(response.getJSONObject("date")));
                    title.setText(response.getString("name"));
                    desc.setText(response.getString("description"));
                    date.setText(dateParser.parseResponseDate(response.getJSONObject("date")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                usersFragment.passDataToFragment(response);
                usersFragment.displayAttendees();
            }
        });

        turndown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAttendanceRequest.setRequestBody(attendanceEditRequestBody(false));
                editAttendanceRequest.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
                        usersFragment.passDataToFragment(response);
                        usersFragment.displayAttendees();
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
                        usersFragment.passDataToFragment(response);
                        usersFragment.displayAttendees();
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

}
