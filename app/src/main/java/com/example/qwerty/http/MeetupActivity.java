package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qwerty on 4.11.2015.
 */
public class MeetupActivity extends Activity {

    JSONObject meetup = new JSONObject();
    JSONObject uidJson = new JSONObject();
    JSONArray users = new JSONArray();
    ArrayList<JSONObject> userList = new ArrayList<>();
    ListView listview;
    Button createBtn;
    Button deleteBtn;
    Button invBtn;
    TextView title;
    TextView desc;
    TextView invite;
    RequestCondenser inviteRequest;
    RequestCondenser createRequest;
    RequestCondenser deleteRequest;
    RequestCondenser editRequest;
    String uid;
    Context ctx = this;

    private static String TAG = MeetupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new);
        uid = getIntent().getExtras().getString("_id");
        invBtn = (Button) findViewById(R.id.invButton);
        createBtn = (Button) findViewById(R.id.createButton);
        deleteBtn = (Button) findViewById(R.id.delBut);

        listview = (ListView) findViewById(R.id.usrList);
        desc = (TextView) findViewById(R.id.descTxt);
        title = (TextView) findViewById(R.id.titleTxt);
        invite = (TextView) findViewById(R.id.invTxt);

        inviteRequest = new RequestCondenser(
                Request.Method.POST,
                getString(R.string.apiUrl).concat("/meetup/invite"),
                TAG,
                ctx
        );
        createRequest = new RequestCondenser(
                Request.Method.POST,
                populateCreateRequestBody(),
                getString(R.string.apiUrl).concat("/meetup/create"),
                TAG,
                ctx
        );

        deleteRequest = new RequestCondenser(
                Request.Method.POST,
                null,
                getString(R.string.apiUrl).concat("/meetup/delete"),
                TAG,
                ctx
        );

        editRequest = new RequestCondenser(
                Request.Method.POST,
                getString(R.string.apiUrl).concat("/meetup/edit"),
                TAG,
                ctx
        );

        createRequest.request(new RequestCondenser.ActionOnResponse() {
            @Override
            public void responseCallBack(JSONObject response) {
                Toast.makeText(getApplicationContext(),
                        "Meetup successfully created!" +
                        " Now to add content and people to it!", Toast.LENGTH_LONG).show();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRequest.setRequestBody(populateEditRequestBody());
                editRequest.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
                        Toast.makeText(getApplicationContext(),
                                "Meetup successfully edited!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRequest.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
                        Intent intent = new Intent(MeetupActivity.this, MainActivity.class);
                        try {
                            intent.putExtra("name", response.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            }
        });

        invBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteRequest.setRequestBody(populateInviteRequestBody());
                inviteRequest.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
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
                                UserArrayAdapter adapter = new UserArrayAdapter(ctx, userList);
                                // set data to listView with adapter
                                listview.setAdapter(adapter);
                            }
                            else
                                Toast.makeText(ctx,
                                "No such user exists!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private JSONObject populateCreateRequestBody () {
        try {
            return uidJson.put("_id", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uidJson;
    }

    private JSONObject populateEditRequestBody () {
        try {
            meetup.put("description", desc.getText().toString());
            meetup.put("name", title.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meetup;
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
}
