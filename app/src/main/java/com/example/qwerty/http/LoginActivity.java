package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qwerty on 2.11.2015.
 */
public class LoginActivity extends Activity {

    Button submitButton;
    EditText emailField;
    EditText uNameField;
    JSONObject requestData = new JSONObject();

    Context ctx = this;
    DBHelper db;
    Cursor c;

    RequestCondenser SignInOrSignUp;

    private static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        displayNoAccountToast();

        submitButton = (Button) findViewById(R.id.submitButton);
        emailField = (EditText)findViewById(R.id.emailInput);
        uNameField = (EditText)findViewById(R.id.uNameInput);

        SignInOrSignUp = new RequestCondenser(
                Request.Method.POST,
                getString(R.string.apiUrl).concat("/user/entry"),
                TAG,
                ctx
        );


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInOrSignUp.setRequestBody(populateRequestData());
                SignInOrSignUp.request(new RequestCondenser.ActionOnResponse() {
                    @Override
                    public void responseCallBack(JSONObject response) {
                        try {
                            c = db.getUser(response.getString("_id"));
                            if (!c.moveToNext())
                                db.setData(response.getString("_id"));
                            c.close();
                            if(response.has("_id")) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private JSONObject populateRequestData () {
        try {
            requestData.put("name", uNameField.getText().toString());
            requestData.put("_email", emailField.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestData;
    }

    private void displayNoAccountToast() {
        if(getIntent().hasExtra("noaccount"))
            Toast.makeText(ctx,
                    "This device does not contain any accounts for Login." +
                    "Please Login or Register here.",
                    Toast.LENGTH_LONG)
                .show();
    }
}


