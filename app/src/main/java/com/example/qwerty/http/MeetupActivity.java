package com.example.qwerty.http;

import android.app.Activity;
import android.app.FragmentManager;
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
    JSONObject editData = new JSONObject();
    JSONObject date = new JSONObject();
    JSONObject dataObj = new JSONObject();

    private final int GET_DATE = 1;

    Button editBtn;
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

    FragmentManager fm = getFragmentManager();
    DataRetainFragment retainFragment;
    Context ctx = this;

    private static String TAG = MeetupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new);
        invBtn = (Button) findViewById(R.id.invButton);
        editBtn = (Button) findViewById(R.id.editButton);
        deleteBtn = (Button) findViewById(R.id.delBut);
        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        dateBtn = (Button) findViewById(R.id.dateButton);
        desc = (TextView) findViewById(R.id.descTxt);
        dateTxt = (TextView) findViewById(R.id.date_txt);
        title = (TextView) findViewById(R.id.titleTxt);

        usersFragment = (UserListFragment) fm.findFragmentById(R.id.list);

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
                getData(true);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
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
                invDialog.show(fm, "Invite people");
            }
        });

        retainFragment = (DataRetainFragment) fm.findFragmentByTag("retain_fragment");

        if (retainFragment == null) {
            retainFragment = new DataRetainFragment();
            fm.beginTransaction().add(retainFragment, "retain_fragment").commit();

            if(getIntent().hasExtra("uid")) {
                createRequest.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
                        Toast.makeText(getApplicationContext(),
                                "Meetup successfully created!" +
                                        " Now to add content and people to it!",
                                Toast.LENGTH_LONG).show();
                        getData(false);
                    }
                });
            } else getData(true);
        }
        else {
            JSONObject dataToShow = retainFragment.getData();
            try {
                dateTxt.setText(dataToShow.getString("parsedDate"));
                usersFragment.passDataToFragment(dataToShow.getJSONObject("meetup"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_DATE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            try {
                date = new JSONObject().put("date", new JSONObject(extras.getString("json")));
                retainFragment.setData(true, date);
                dateTxt.setText(retainFragment.getParsedDate());
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
                else {
                    try {
                        dataObj.put("meetup", response);
                        retainFragment.setData(false, dataObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    usersFragment.passDataToFragment(response);
                }
            }
        });
    }

    private JSONObject populatedGetRequestBody() {
        try {
            if(getIntent().getExtras().containsKey("_id"))
                idJson.put("_id", getIntent().getExtras().getString("_id"));
            else idJson = new JSONObject();
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
                editData.put("_id", getIntent().getExtras().getString("_id"));
            if(retainFragment.getRawDate() != null)
                editData.put("date", retainFragment.getRawDate());
            editData.put("description", desc.getText().toString());
            editData.put("name", title.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return editData;
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

    public void getData(final boolean displayData) {
        getDataRequest.setRequestBody(populatedGetRequestBody());
        getDataRequest.request(new RequestCondenser.ActionOnResponse() {
            @Override
            public void responseCallBack(JSONObject response) {
            try {
                dataObj.put("meetup", response);
                dataObj.put("date", response.getJSONObject("date"));
                retainFragment.setData(false, dataObj);
                dateTxt.setText(retainFragment.getParsedDate());
                if(displayData) {
                    title.setText(response.getString("name"));
                    desc.setText(response.getString("description"));
                }

                usersFragment.passDataToFragment(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }
}