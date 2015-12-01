package com.example.qwerty.http;

/**
 * Created by qwerty on 30.11.2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

/**
 * This fragment is primarily made for filtering any function call(s) desired upon an activity's
 * configuration changes. At this moment, its only instantiator is MeetupActivity, and its
 * function there is to prevent meetup creation upon configuration changes(device rotation).
 * This fragment is easily expandable for more complex use in the future.
 */
public class DataRetainFragment extends Fragment {

    CallBack callBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Reference to the parent activity
        callBack = (CallBack) activity;
    }

    interface CallBack {
        void retainableCallBack();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        callBack.retainableCallBack();
    }
}
