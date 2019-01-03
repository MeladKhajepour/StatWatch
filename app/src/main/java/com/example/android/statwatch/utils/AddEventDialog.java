package com.example.android.statwatch.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.statwatch.MainActivity;
import com.example.android.statwatch.R;

import java.util.ArrayList;

public class AddEventDialog extends DialogFragment {
    private EditText hours;
    private EditText mins;
    private EditText secs;
    private EditText tenths;
    private EditText nextInput;
    private TextWatcher tw;
    private View.OnFocusChangeListener fcl;
    private AlertDialog.Builder builder;
    private View dialogView;

    public AddEventDialog() {

        //todo - set nextInput

        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2) {
                    nextInput.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        fcl = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()) {
                    case R.id.dialog_hours:
                        nextInput = mins;
                        break;

                    case R.id.dialog_mins:
                        nextInput = secs;
                        break;

                    case R.id.dialog_secs:
                        nextInput = tenths;
                        break;
                }
            }
        };
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_event_dialog, null);
        hours = dialogView.findViewById(R.id.dialog_hours);
        mins = dialogView.findViewById(R.id.dialog_mins);
        secs = dialogView.findViewById(R.id.dialog_secs);
        tenths = dialogView.findViewById(R.id.dialog_tenths);

        hours.setTransformationMethod(null);
        mins.setTransformationMethod(null);
        secs.setTransformationMethod(null);
        tenths.setTransformationMethod(null);

        hours.addTextChangedListener(tw);
        mins.addTextChangedListener(tw);
        secs.addTextChangedListener(tw);
        tenths.addTextChangedListener(tw);

        hours.setOnFocusChangeListener(fcl);
        mins.setOnFocusChangeListener(fcl);
        secs.setOnFocusChangeListener(fcl);
        tenths.setOnFocusChangeListener(fcl);

        final ArrayList<EditText> list = new ArrayList<>();
        list.add(hours);
        list.add(mins);
        list.add(secs);
        list.add(tenths);


        hours.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        builder.setTitle("Add event");
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        long millis = 0;

                        for(EditText et : list) {
                            String entry = et.getText().toString();
                            long value;

                            if(entry.equals("")) {
                                value = 0;
                            } else {
                                value = Integer.valueOf(entry);
                            }

                            switch (et.getId()) {
                                case R.id.dialog_hours:
                                    millis += value * 3600000;
                                    break;

                                case R.id.dialog_mins:
                                    millis += value * 60000;
                                    break;

                                case R.id.dialog_secs:
                                    millis += value * 1000;
                                    break;

                                case R.id.dialog_tenths:
                                    millis += value * 100;
                                    break;

                            }
                        }

                        if(millis > 0) {
                            ((MainActivity) getActivity()).addEvent(millis);
                        } else {
                            Toast.makeText(getContext(), "Duration must be greater than 0", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddEventDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
