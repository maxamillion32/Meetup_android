package com.example.qwerty.http;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

public class DateActivity extends Activity {
    Calendar calendar = Calendar.getInstance();
    TextView display;
    JSONObject jsonAll = new JSONObject();
    JSONObject jsonFrom = new JSONObject();
    JSONObject jsonTo = new JSONObject();
    JSONObject jsonTimeTo = new JSONObject();
    JSONObject jsonDateTo = new JSONObject();
    JSONObject jsonTimeFrom = new JSONObject();
    JSONObject jsonDateFrom = new JSONObject();

    Button startDateButton;
    Button endDateButton;
    Button startTimeButton;
    Button endTimeButton;
    Button doneButton;

    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates);

        endDateButton = (Button)findViewById(R.id.button_endDate);
        startDateButton = (Button)findViewById(R.id.button_startDate);
        startTimeButton = (Button)findViewById(R.id.button_startTime);
        endTimeButton = (Button)findViewById(R.id.button_endTime);
        doneButton = (Button)findViewById(R.id.done_button);
        // START DATE

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DateActivity.this, listener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                display = (TextView) findViewById(R.id.textView_startDate);
                str = "from";
            }
        });
        // END DATE

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DateActivity.this, listener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                display = (TextView)findViewById(R.id.textView_endDate);
                str = "to";
            }
        });
        // START TIME

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(DateActivity.this, onTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                display = (TextView)findViewById(R.id.textView_startTime);
                str = "from";
            }

        });
        // END TIME

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(DateActivity.this, onTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                display = (TextView)findViewById(R.id.textView_endTime);
                str = "to";
            }

        });

        // DONE BUTTON

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(
                    (
                        (jsonDateFrom.length() != 0 && jsonDateTo.length() != 0) ||
                        (jsonDateFrom.length() == 0 && jsonDateTo.length() == 0)
                    ) &&
                    (jsonTimeFrom.length() != 0 && jsonTimeTo.length() != 0)
                ) {
                    Intent intent = new Intent();
                    intent.putExtra("json", jsonAll.toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),
                            "Not all of the required times and/or dates were set!",
                            Toast.LENGTH_LONG)
                        .show();
            }

        });

    } // /onCreate

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            display.setText(String.format(" %d:%d ", hourOfDay, minute));



            if (Objects.equals(str, "from")) {
                try {
                    jsonTimeFrom.put("mins", minute);
                    jsonTimeFrom.put("hrs", hourOfDay);
                    jsonFrom.put("time", jsonTimeFrom);
                    jsonAll.put("from", jsonFrom);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (Objects.equals(str, "to")) {
                try {
                    jsonTimeTo.put("mins", minute);
                    jsonTimeTo.put("hrs", hourOfDay);
                    jsonTo.put("time", jsonTimeTo);
                    jsonAll.put("to", jsonTo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            display.setText(String.format(" %d/%d/%d ", dayOfMonth, monthOfYear + 1, year));
            if (Objects.equals(str, "from")) {

                try {
                    jsonDateFrom.put("d", dayOfMonth);
                    jsonDateFrom.put("mon", monthOfYear);
                    jsonDateFrom.put("yr", year);
                    jsonFrom.put("date", jsonDateFrom);
                    jsonAll.put("from", jsonFrom);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (Objects.equals(str, "to")) {

                try {
                    jsonDateTo.put("d", dayOfMonth);
                    jsonDateTo.put("mon", monthOfYear);
                    jsonDateTo.put("yr", year);
                    jsonTo.put("date", jsonDateTo);
                    jsonAll.put("to", jsonTo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
