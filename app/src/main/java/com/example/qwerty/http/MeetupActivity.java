package com.example.qwerty.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qwerty on 4.11.2015.
 */
public class MeetupActivity extends Activity implements InviteDialog.InviteListener {

    JSONObject idJson = new JSONObject();
    JSONObject meetup = new JSONObject();
    JSONObject date = new JSONObject();
    DateParser dateParser = new DateParser();
    private final int GET_DATE = 1;
    Button createBtn;
    Button deleteBtn;
    Button refreshBtn;
    Button invBtn;
    Button dateBtn;
    TextView title;
    TextView desc;
    TextView dateTxt;
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
        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        dateBtn = (Button) findViewById(R.id.dateButton);
        desc = (TextView) findViewById(R.id.descTxt);
        dateTxt = (TextView) findViewById(R.id.date_txt);
        title = (TextView) findViewById(R.id.titleTxt);
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
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetupActivity.this, DateActivity.class);
                startActivityForResult(intent, GET_DATE);
            }
        });


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

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
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
                InviteDialog invDialog = new InviteDialog();
                invDialog.show(getFragmentManager(), "Invite people");
            }
        });

        if (getIntent().hasExtra("uid")) {
            createRequest.request(new RequestCondenser.ActionOnResponse() {
                @Override
                public void responseCallBack(JSONObject response) {
                    Toast.makeText(getApplicationContext(),
                            "Meetup successfully created!" +
                                    " Now to add content and people to it!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            getData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_DATE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            try {
                date = new JSONObject(extras.getString("json"));
                dateTxt.setText(dateParser.parseSetDate(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInvBtnClick (String invitee){
        inviteRequest.setRequestBody(populateInviteRequestBody(invitee));
        inviteRequest.request(new RequestCondenser.ActionOnResponse() {
            @Override
            public void responseCallBack(JSONObject response) {
                if (response.length() == 0)
                    Toast.makeText(ctx,
                            "Either no such user exists or is already a part of this meetup!",
                            Toast.LENGTH_LONG).show();

                usersFragment.passDataToFragment(response);
            }
        });
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
    //the if-clause for the _id is a check on whether this editing session
    //is an edit session or in fact a create session. The API is programmed
    //to check for a meetup _id within the request body and to act accordingly.
    private JSONObject populateEditRequestBody () {
        try {
            if(getIntent().hasExtra("_id"))
                meetup.put("_id", getIntent().getExtras().getString("_id"));
            if(date.length() != 0)
                meetup.put("date", date);
            meetup.put("description", desc.getText().toString());
            meetup.put("name", title.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meetup;
    }

    //to be clear here, the _id stands for the meetings _id to which the user will be invited.
    private JSONObject populateInviteRequestBody (String invitee) {
        JSONObject user = new JSONObject();
        try {
            if(getIntent().hasExtra("_id"))
                user.put("_id", getIntent().getExtras().getString("_id"));
            user.put("_email", invitee);
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

    public void getData() {
        getDataRequest.setRequestBody(populatedGetRequestBody());
        getDataRequest.request(new RequestCondenser.ActionOnResponse() {
            @Override
            public void responseCallBack(JSONObject response) {
                try {
                    title.setText(response.getString("name"));
                    desc.setText(response.getString("description"));
                    dateTxt.setText(dateParser.parseResponseDate(response.getJSONObject("date")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                usersFragment.passDataToFragment(response);
            }
        });
    }
}