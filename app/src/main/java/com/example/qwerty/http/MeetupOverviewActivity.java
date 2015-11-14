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

/**
 * Created by qwerty on 12.11.2015.
 */
public class MeetupOverviewActivity extends Activity {

    ProgressDialog pDialog;
    Bundle extras;
    JSONObject meetup = new JSONObject();
    JSONArray users = new JSONArray();
    ArrayList<JSONObject> userList = new ArrayList<>();
    ListView listview;
    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetup_overview);

        extras = getIntent().getExtras();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        requestMeetup(this);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private JSONObject populatedGetRequestBody() {
        try {
            meetup.put("_id", extras.getString("meetup"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meetup;
    }

    private JSONObject attendanceEditRequestBody() {
        try {
            meetup.put("meetup", extras.getString("meetup"));
            meetup.put("_id", extras.getString("uid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meetup;
    }

    private void requestMeetup(final Context context) {

        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.apiUrl).concat("/meetup/get"),
                populatedGetRequestBody(), new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                if(response != null)
                    generateUserList(response, context);
                hidepDialog();

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
                headers.put("charset", "utf-8");
                return headers;
            }
        };

        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(req);
    }

    private void requestAttendanceEdit(final Context context) {

        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.apiUrl).concat("/user/attendance"),
                attendanceEditRequestBody(), new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                if(response != null)
                    generateUserList(response, context);
                hidepDialog();

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
                headers.put("charset", "utf-8");
                return headers;
            }
        };

        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(req);
    }

    private void generateUserList(JSONObject response, Context context) {
        try {
            users = response.getJSONArray("users");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        userList.clear();

        for (int i = 0; i < users.length(); ++i) {
            try {
                userList.add(users.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // add data to ArrayAdapter
        UserArrayAdapter adapter = new UserArrayAdapter(context, userList);
        // set data to listView with adapter
        listview.setAdapter(adapter);

        // hide the progress dialog
        hidepDialog();

    }

}
