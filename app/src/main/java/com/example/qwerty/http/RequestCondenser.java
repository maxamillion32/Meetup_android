package com.example.qwerty.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qwerty on 14.11.2015.
 */
public class RequestCondenser {
    int reqMethod;
    JSONObject body;
    String url;
    String TAG;
    Context ctx;

    // A version of the constructor without a request body. Often the body is set
    // later on, since it needs to contain things like user-set data.
    public RequestCondenser(int method, String url, String TAG, Context ctx) {
        this.reqMethod = method;
        this.url = url;
        this.TAG = TAG;
        this.ctx = ctx;
    }

    // A version of the constructor including the request body.
    public RequestCondenser(int method, JSONObject body, String url, String TAG, Context ctx) {
        this.reqMethod = method;
        this.body = body;
        this.url = url;
        this.TAG = TAG;
        this.ctx = ctx;
    }

    public void setRequestBody(JSONObject body) {
        this.body = body;
    }

    interface ActionOnResponse {
        void responseCallBack(JSONObject response);
    }

    public void request(final ActionOnResponse cb) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cb.responseCallBack(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Error message gets handled and displayed to the user here,
                //setting the message is up to the server.
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    Toast.makeText(
                            ctx,
                            String.valueOf(volleyError.networkResponse.statusCode)
                            + ": " + error.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                Log.d(TAG, "" + volleyError.getMessage() + "," + volleyError.toString());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                return headers;
            }
        };
        MySingleton.getInstance(ctx).addToRequestQueue(req);
    }
}
