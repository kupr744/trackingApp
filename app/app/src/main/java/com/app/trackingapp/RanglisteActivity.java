package com.app.trackingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RanglisteActivity extends FragmentActivity {
    private boolean loads = true;
    private ProgressBar pb;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private String email;
    private String jogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rangliste);
        pb = findViewById(R.id.progressBar2);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            email = bundle.getString("email");
            jogger = bundle.getString("user");
        }

        loading();

        new Handler().postDelayed(this::init, 2000);
    }

    private void init() {
        List<Pair<String, Double>> data = new ArrayList<>();

        ref.child("users").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                System.out.println("firebase" + "Error getting data" + task.getException());
            }
            else {
                TableLayout tl = (TableLayout) findViewById(R.id.tableLayoutScore);

                System.out.println("firebase" + String.valueOf(task.getResult().getValue()));
                for(DataSnapshot i : task.getResult().getChildren()) {
                    UserClass usr = i.getValue(UserClass.class);
                    data.add(new Pair<>(usr.getUsername(), usr.getKm()));
                }


                data.sort(new Comparator<Pair<String, Double>>() {
                    @Override
                    public int compare(Pair<String, Double> x, Pair<String, Double> y) {
                        return Double.compare(y.second, x.second);
                    }
                });

                for(int i=0; i<data.size(); i++) {
                    System.out.println("Debug: Init: " + data.get(i).first + data.get(i).second);
                    TableRow tr = new TableRow(this);
                    TextView tv_no = new TextView(this);
                    tv_no.setText(String.valueOf(i+1));
                    tv_no.setTextColor(Color.BLACK);
                    TextView tv_user = new TextView(this);
                    tv_user.setText(data.get(i).first);
                    tv_user.setTextColor(Color.BLACK);
                    TextView tv_weg = new TextView(this);
                    tv_weg.setText(String.format("%.2f", data.get(i).second));
                    tv_weg.setTextColor(Color.BLACK);
                    tr.addView(tv_no);
                    tr.addView(tv_user);
                    tr.addView(tv_weg);
                    tl.addView(tr);
                }
                loading();
            }
        });
    }

    private List<Pair<String, Double>> getData() {
        List<Pair<String, Double>> res = new ArrayList<>();



        System.out.println("data: " + res);
        return res;
    }

    private void loading() {
        if(loads && pb != null) {
            // view spinner
            pb.setVisibility(TextView.VISIBLE);
            loads = false;
        } else if(!loads && pb != null){
            // stop spinner
            pb.setVisibility(TextView.GONE);
        }
    }
}
