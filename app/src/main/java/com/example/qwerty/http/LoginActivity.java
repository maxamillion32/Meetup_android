package com.example.qwerty.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
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
    TextView responseSpace;
    EditText emailField;
    EditText uNameField;
    ProgressDialog pDialog;
    JSONObject requestData = new JSONObject();
    JSONArray meetings = new JSONArray();
    DBHelper db;

    private static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DBHelper(this);
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
            requestData.put("name", uNameField.getText().toString());
            requestData.put("_email", emailField.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestData;
    }


    private void makeRequest() {
        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.apiUrl).concat("/userentry"),
                populateRequestData(), new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                //responseSpace.setText(response.toString());
                hidepDialog();
                try {
                    meetings = response.getJSONArray("meetings");

                    db.setData(response.getString("_id"), true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

               try {
                    Cursor c = db.getUser(response.getString("_id"));
                    String asd = "";
                    if(c.isBeforeFirst())c.moveToNext();
                    while(!c.isAfterLast()) {
                        asd +=c.getString(c.getColumnIndex("uid"));
                        c.moveToNext();
                   }
                    responseSpace.setText(asd);

               } catch (Exception e) {
                    e.printStackTrace();
               }

                //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                //intent.putExtra("meetings", meetings.toString());
                //startActivity(intent);
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

}


