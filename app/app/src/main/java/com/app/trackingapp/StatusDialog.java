package com.app.trackingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.print.PageRange;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class StatusDialog extends DialogFragment {
    private String mDuration;
    private String mSteps;
    private String mDistance;
    private String mCal;
    private Activity activity;

    public StatusDialog(String aCalories, String aDuration, String aSteps, String aDistance, Activity aActivity) {
        this.mCal = aCalories;
        this.mDuration = aDuration;
        this.mSteps = aSteps;
        this.mDistance = aDistance;
        this.activity = aActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Statistics")
                .setMessage("burned calories: " + mCal + "\n" +
                        "duration: " + mDuration + "\n" +
                        "steps: " + mSteps + "\n" +
                        "distance: " + mDistance + "\n")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismissAllowingStateLoss();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
