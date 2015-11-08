package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qwerty on 4.11.2015.
 */
public class MeetupActivity extends Activity {

    DBHelper db;
    Cursor c;
    JSONObject meeting = new JSONObject();
    Button createBtn;
    Button invBtn;
    TextView title;
    TextView desc;
    TextView invite;

    ProgressDialog pDialog;

    String uid;


    private static String TAG = MeetupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new);

        db = new DBHelper(this);
        invBtn = (Button) findViewById(R.id.invButton);
        createBtn = (Button) findViewById(R.id.createButton);
        createBtn = (Button) findViewById(R.id.createButton);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        desc = (TextView) findViewById(R.id.descTxt);
        title = (TextView) findViewById(R.id.titleTxt);
        invite = (TextView) findViewById(R.id.invTxt);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = db.getActiveUser();
                if(c.isBeforeFirst())
                    c.moveToNext();
                uid = c.getString(c.getColumnIndex("uid"));
                c.close();

                makeCreateRequest();
            }
        });

        invBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeInviteRequestOnNew();
            }
        });

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private JSONObject populateCreateRequestBody () {
        try {
            meeting.put("description", desc.getText().toString());
            meeting.put("name", title.getText().toString());
            meeting.put("_id", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meeting;
    }

    private JSONObject populateInviteRequestBody () {
        JSONObject user = new JSONObject();
        try {
            user.put("_email", invite.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    private void makeCreateRequest() {

        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.apiUrl).concat("/meetup/create"),
                populateCreateRequestBody(), new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),
                        "Meetup successfully created!", Toast.LENGTH_SHORT).show();
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


    private void makeInviteRequestOnExisting() {

        showpDialog();

        StringRequest sr = new StringRequest(Request.Method.POST,
                getString(R.string.apiUrl).concat("/meetup/edit"),
                populateInviteRequestBody(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "" + error.getMessage() + "," + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                //this id is passed from the meetup listing in mainactivity
               // params.put("_id", meetingId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "text/html");
                headers.put("charset", "utf-8");
                return headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(sr);
    }

    private void makeInviteRequestOnNew() {

        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.apiUrl).concat("/meetup/invite"),
                populateInviteRequestBody(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "" + error.getMessage() + "," + error.toString());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                return headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(req);
    }
}
