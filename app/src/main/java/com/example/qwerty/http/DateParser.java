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
    //kinds of display formats for date objects at the moment.
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
        JSONObject objWithDates = null;
        try {
            objWithDates = dateObj.getJSONObject("from").getJSONObject("date");
        } catch (JSONException e){
            //ignore
        }

        try {
            textToShow = objWithDates == null ?
                textToShow.concat( "Begins today at: " +
                    dateObj.getJSONObject("from").getJSONObject("time").getInt("hrs") + ":" +
                    dateObj.getJSONObject("from").getJSONObject("time").getInt("mins") +
                    ", Ends today at: " + dateObj.getJSONObject("to").getJSONObject("time").getInt("hrs")
                    + ":" + dateObj.getJSONObject("to").getJSONObject("time").getInt("mins") + "."
                ) :
                textToShow.concat(
                    dateObj.getJSONObject("from").getJSONObject("date").getInt("d") + "/" +
                    (dateObj.getJSONObject("from").getJSONObject("date").getInt("mon") + 1) + "/" +
                    dateObj.getJSONObject("from").getJSONObject("date").getInt("yr") + " " +
                    dateObj.getJSONObject("from").getJSONObject("time").getInt("hrs") + ":" +
                    dateObj.getJSONObject("from").getJSONObject("time").getInt("mins") + " - " +
                    dateObj.getJSONObject("to").getJSONObject("date").getInt("d") + "/" +
                    (dateObj.getJSONObject("to").getJSONObject("date").getInt("mon") + 1) + "/" +
                    dateObj.getJSONObject("to").getJSONObject("date").getInt("yr") + " " +
                    dateObj.getJSONObject("to").getJSONObject("time").getInt("hrs") + ":" +
                    dateObj.getJSONObject("to").getJSONObject("time").getInt("mins") + "."
                );

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(DateParser.class.getSimpleName(), dateObj.toString());
        return textToShow;
    }



}