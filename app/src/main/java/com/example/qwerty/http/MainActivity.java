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

    ProgressDialog pDialog;
    JSONArray meetUps;
    ArrayList<JSONObject> meetupList = new ArrayList<>();
    ListView listview;
    DBHelper db;
    Cursor c;

    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);

        listview = (ListView) findViewById(R.id.listView);
        btnMakeObjectRequest = (Button) findViewById(R.id.btnObjRequest);
        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        meetingCreation = (Button) findViewById(R.id.createMeetButton);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        view.getTag().toString(), Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);


            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestCaller();
            }
        });
        requestCaller();
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

    private void getMeetUps(final Context context) {
        showpDialog();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.apiUrl).concat("/user/meetups"),
                getRequestData(), new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                hidepDialog();
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
                MeetupArrayAdapter adapter = new MeetupArrayAdapter(context, meetupList);
                // set data to listView with adapter
                listview.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put( "charset", "utf-8");
                return headers;
            }
        };
        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(req);

    }

    private void requestCaller() {
        getMeetUps(this);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}