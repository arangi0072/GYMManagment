package com.arpit.gymmanagment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.arpit.gymmanagment.Model.Plans;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlanDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Intent intent = getIntent();
        String plan = intent.getStringExtra("plan");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Plans plans = new Plans();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase.child(user).child("Plans").child(plan).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    HashMap data = (HashMap) task.getResult().getValue();
                    String planDetails = (String) data.get("plan_details");
                    String time = (String) data.get("time");
                    String fee = (String) data.get("fee");
                    plans.setPlan_details(planDetails);
                    plans.setTime(time);
                    Log.d("firebase" , ""+task.getResult().getValue()+planDetails+time+fee);
                    plans.setFee(fee);
                    TextView plan_name = findViewById(R.id.plan_des);
                    TextView plan_time = findViewById(R.id.plan_time);
                    EditText plan_fee = findViewById(R.id.plan_fee);
                    String time1 = plans.getTime() + " Months";
                    plan_name.setText(plans.getPlan_details());
                    plan_time.setText(time1);
                    plan_fee.setText(plans.getFee());
                }
            }
        });
        ImageButton back = findViewById(R.id.back);
        ImageButton update = findViewById(R.id.updateButton);
        FloatingActionButton fab = findViewById(R.id.deleteButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(user).child("Plans").child(plan).removeValue();
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText plan_fee = findViewById(R.id.plan_fee);
                plans.setFee(plan_fee.getText().toString());
                Map<String, Object> postValues = plans.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(user + "/" + "Plans" + "/" +plan, postValues);
                mDatabase.updateChildren(childUpdates);
                Toast.makeText(PlanDetails.this, "Update Successful!" , Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}