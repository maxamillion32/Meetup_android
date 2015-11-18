package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    Button refreshBtn;
    Button meetingCreation;
    Button btnMakeObjectRequest;

    JSONArray meetUps;
    ArrayList<JSONObject> meetupList = new ArrayList<>();
    ListView listview;
    DBHelper db;
    Cursor c;
    Intent intent;
    Context ctx = this;

    RequestCondenser getMeetUps;

    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();
        sendRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);
        if (!db.getAllUsers().moveToNext()) {
            db.getAllUsers().moveToPrevious();
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        listview = (ListView) findViewById(R.id.listView);
        btnMakeObjectRequest = (Button) findViewById(R.id.btnObjRequest);
        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        meetingCreation = (Button) findViewById(R.id.createMeetButton);

        getMeetUps = new RequestCondenser(
                Request.Method.POST,
                getString(R.string.apiUrl).concat("/user/meetups"),
                TAG,
                ctx
        );

        sendRequest();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MeetupOverviewActivity.class);
                intent.putExtra("meetup", view.getTag().toString());
                intent.putExtra("uid", c.getString(c.getColumnIndex("uid")));
                startActivity(intent);
            }
        });

        meetingCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = db.getActiveUser();
                c.moveToNext();
                // launch meetup activity
                Intent intent = new Intent(MainActivity.this, MeetupActivity.class);
                intent.putExtra("_id", c.getString(c.getColumnIndex("uid")));
                startActivity(intent);
            }
        });

        btnMakeObjectRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //launch login activity
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendRequest();
                }
            });
    }

    private JSONObject getRequestData() {

        JSONObject obj = new JSONObject();

        c = db.getActiveUser();
        c.moveToNext();
        try {
            obj.put("_id", c.getString(c.getColumnIndex("uid")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
            return obj;
    }

    private void sendRequest() {
        getMeetUps.setRequestBody(getRequestData());
        getMeetUps.request(new RequestCondenser.ActionOnResponse() {
            @Override
            public void responseCallBack(JSONObject response) {
                try {
                    meetUps = response.getJSONArray("meetings");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                meetupList.clear();

                for (int i = 0; i < meetUps.length(); ++i) {
                    try {
                        meetupList.add(meetUps.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // add data to ArrayAdapter
                MeetupArrayAdapter adapter = new MeetupArrayAdapter(
                                                    ctx,
                                                    meetupList,
                                                    c.getString(c.getColumnIndex("uid")),
                                                    getString(R.string.apiUrl)
                                            );
                // set data to listView with adapter
                listview.setAdapter(adapter);
            }
        });
    }


}