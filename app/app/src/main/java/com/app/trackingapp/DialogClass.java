package com.app.trackingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.print.PageRange;

import androidx.fragment.app.DialogFragment;

public class DialogClass extends DialogFragment {
    private String mTitle;
    private String mMessage;
    private boolean agreed = false;

    public DialogClass(String aTitle, String aMessage) {
        this.mTitle = aTitle;
        this.mMessage = aMessage;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setTitle(this.mTitle)
            .setMessage(this.mMessage)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    agreed = true;
                    dismiss();
                }
            })
            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dismiss();
                }
            });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public boolean hasAgreed() {
        return this.agreed;
    }
}
