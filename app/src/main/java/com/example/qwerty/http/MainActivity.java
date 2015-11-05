package com.example.qwerty.http;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity {

    Button btnMakeObjectRequest;
    JSONArray meetUps = new JSONArray();
    ArrayList<String> meetupList = new ArrayList<String>();
    ListView listview = (ListView) findViewById(R.id.listView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginIntent = getIntent();
        btnMakeObjectRequest = (Button) findViewById(R.id.btnObjRequest);
        btnMakeObjectRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // launch login activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        if(loginIntent.getStringExtra("meetings") != null) {
            // find list view
            try {
                meetUps = new JSONArray(loginIntent.getStringExtra("meetings"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // add data to ArrayList
            for (int i = 0; i < meetUps.length(); ++i) {
                try {
                    meetupList.add(meetUps.getJSONObject(i).getString("title"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
            // add data to ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.textView, meetupList);
            // set data to listView with adapter
            listview.setAdapter(adapter);
    }
}