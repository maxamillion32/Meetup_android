package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qwerty on 2.11.2015.
 */
public class LoginActivity extends Activity {

    Button submitButton;
    TextView responseSpace;
    EditText emailField;
    EditText uNameField;
    ProgressDialog pDialog;
    JSONObject requestData = new JSONObject();

    private static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submitButton = (Button) findViewById(R.id.submitButton);
        responseSpace = (TextView) findViewById(R.id.resultSpace);
        emailField = (EditText)findViewById(R.id.emailInput);
        uNameField = (EditText)findViewById(R.id.uNameInput);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
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

    private JSONObject populateRequestData () {
        try {
            requestData.put("username", uNameField.getText().toString());
            requestData.put("_email", emailField.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestData;
    }


    private void makeRequest() {
        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                getResources().getString(R.string.apiUrl),
                populateRequestData(), new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                // Parsing json object response
                // response will be a json object
                VolleyLog.d("json", response);
                responseSpace.setText(response.toString());


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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put( "charset", "utf-8");
                return headers;
            }
        };

        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(req);
    }


}
