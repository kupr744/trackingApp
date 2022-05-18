package com.app.trackingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

public class RanglisteActivity extends FragmentActivity {
    private boolean loads = true;
    private ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rangliste);
        pb = findViewById(R.id.progressBar2);

        loading();

        new Handler().postDelayed(this::init, 2000);
    }

    private void init() {
        loading();

        TableLayout tl = (TableLayout) findViewById(R.id.tableLayoutScore);
        for(int i=1; i<120; i++) {
            TableRow tr = new TableRow(this);
            TextView tv_no = new TextView(this);
            tv_no.setText(String.valueOf(i));
            tv_no.setTextColor(Color.BLACK);
            TextView tv_user = new TextView(this);
            tv_user.setText("test");
            tv_user.setTextColor(Color.BLACK);
            TextView tv_weg = new TextView(this);
            tv_weg.setText("20043");
            tv_weg.setTextColor(Color.BLACK);
            tr.addView(tv_no);
            tr.addView(tv_user);
            tr.addView(tv_weg);
            tl.addView(tr);
        }
    }

    private void loading() {
        if(loads && pb != null) {
            // view spinner
            pb.setVisibility(TextView.VISIBLE);
            loads = false;
        } else {
            // stop spinner
            pb.setVisibility(TextView.GONE);
        }
    }
}
