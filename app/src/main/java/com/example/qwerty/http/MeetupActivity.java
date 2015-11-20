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

    JSONObject idJson = new JSONObject();
    JSONObject meetup = new JSONObject();
    Button createBtn;
    Button deleteBtn;
    Button invBtn;
    TextView title;
    TextView desc;
    TextView invite;
    UserListFragment usersFragment;
    RequestCondenser inviteRequest;
    RequestCondenser createRequest;
    RequestCondenser deleteRequest;
    RequestCondenser editRequest;
    RequestCondenser getDataRequest;
    Context ctx = this;

    private static String TAG = MeetupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new);
        invBtn = (Button) findViewById(R.id.invButton);
        createBtn = (Button) findViewById(R.id.createButton);
        deleteBtn = (Button) findViewById(R.id.delBut);
        desc = (TextView) findViewById(R.id.descTxt);
        title = (TextView) findViewById(R.id.titleTxt);
        invite = (TextView) findViewById(R.id.invTxt);
        usersFragment = (UserListFragment) getFragmentManager().findFragmentById(R.id.list);

        inviteRequest = new RequestCondenser(
                Request.Method.POST,
                getString(R.string.apiUrl).concat("/meetup/invite"),
                TAG,
                ctx
        );

        getDataRequest = new RequestCondenser(
                Request.Method.POST,
                populatedGetRequestBody(),
                getString(R.string.apiUrl).concat("/meetup/get"),
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
                populatedDeleteRequestBody(),
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
                        usersFragment.passDataToFragment(response);
                    }
                });
            }
        });

        if(getIntent().hasExtra("uid")) {
            createRequest.request(new RequestCondenser.ActionOnResponse() {
                @Override
                public void responseCallBack(JSONObject response) {
                    Toast.makeText(getApplicationContext(),
                            "Meetup successfully created!" +
                                    " Now to add content and people to it!", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            getDataRequest.setRequestBody(populatedGetRequestBody());
            getDataRequest.request(new RequestCondenser.ActionOnResponse() {
                @Override
                public void responseCallBack(JSONObject response) {
                    try {
                        title.setText(response.getString("name"));
                        desc.setText(response.getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    usersFragment.passDataToFragment(response);
                }
            });
        }
    }

    private JSONObject populatedGetRequestBody() {
        try {
            idJson.put("_id", getIntent().getExtras().getString("_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return idJson;
    }

    private JSONObject populateCreateRequestBody () {
        try {
            return idJson.put("_id", getIntent().getExtras().getString("uid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return idJson;
    }

    private JSONObject populateEditRequestBody () {
        try {
            if(getIntent().hasExtra("_id"))
                meetup.put("_id", getIntent().getExtras().getString("_id"));
            meetup.put("description", desc.getText().toString());
            meetup.put("name", title.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meetup;
    }

    //to be clear here, the _id stands for the meeting's _id to which the user will be invited.
    private JSONObject populateInviteRequestBody () {
        JSONObject user = new JSONObject();
        try {
            if(getIntent().hasExtra("_id"))
                user.put("_id", getIntent().getExtras().getString("_id"));
            user.put("_email", invite.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    private JSONObject populatedDeleteRequestBody() {
        if(getIntent().hasExtra("_id")) {
            try {
                idJson.put("_id", getIntent().getExtras().getString("_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return idJson;
        } else return null;
    }
 }
