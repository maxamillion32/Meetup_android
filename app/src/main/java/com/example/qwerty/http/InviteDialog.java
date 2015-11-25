package com.example.qwerty.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by h3916 on 24.11.2015.
 */
public class InviteDialog extends DialogFragment {

    interface InviteListener {
        void onInvBtnClick(String teamName);
    }

    InviteListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (InviteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement InviteListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.invite_dialog, null);
        builder.setView(dialogView)
                // Set title
                .setTitle("Invite people")
                        // Add action buttons
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        EditText invTxt = (EditText) dialogView.findViewById(R.id.invTxt);
                        String invitee = invTxt.getText().toString();
                        mListener.onInvBtnClick(invitee);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {/*nothing*/}
                });
        return builder.create();
    }
}
