package com.example.qwerty.http;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by qwerty on 28.11.2015.
 */
public class DateParser {

    //This is getting declared final for now since I do not see use for other
    //kinds of display formats for date objects in this app at the moment.
    //This can easily be reworked later anyway.
    private final String dateFormat = "dd/MM/yyyy HH:mm";

    DateParser() {}

    public String parseResponseDate(JSONObject dateObj) {
        String textToShow = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        try {
            calendar.setTimeInMillis(dateObj.getLong("from"));
            textToShow= formatter.format(calendar.getTime());
            calendar.setTimeInMillis(dateObj.getLong("to"));
            textToShow += " - " + formatter.format(calendar.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textToShow;
    }

    public String parseSetDate(JSONObject dateObj) {

        String textToShow = "";
        try {
            Log.e(DateParser.class.getSimpleName(), dateObj.getJSONObject("from").getJSONObject("date").length() + "");
            textToShow += dateObj.getJSONObject("from").getJSONObject("date").length() == 0 ?
                    "from today at: " :
                    dateObj.getJSONObject("from").getJSONObject("date").getInt("d") + "/" +
                    (dateObj.getJSONObject("from").getJSONObject("date").getInt("mon") + 1) + "/" +
                    dateObj.getJSONObject("from").getJSONObject("date").getInt("yr");
            textToShow += " " +
                    dateObj.getJSONObject("from").getJSONObject("time").getInt("h") + ":" +
                    dateObj.getJSONObject("from").getJSONObject("time").getInt("min") + " - ";

            textToShow += dateObj.getJSONObject("to").getJSONObject("date").length() == 0 ?
                    "to today at: " :
                    dateObj.getJSONObject("to").getJSONObject("date").getInt("d") + "/" +
                    (dateObj.getJSONObject("to").getJSONObject("date").getInt("mon") + 1) + "/" +
                    dateObj.getJSONObject("to").getJSONObject("date").getInt("yr");
            textToShow += " " +
                    dateObj.getJSONObject("to").getJSONObject("time").getInt("h") + ":" +
                    dateObj.getJSONObject("to").getJSONObject("time").getInt("min");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textToShow;
    }



}
