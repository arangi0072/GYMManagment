package com.arpit.gymmanagment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.arpit.gymmanagment.Model.Plans;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AddPlan extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        ActionBar actionBar = this.getSupportActionBar();
        mAuth = FirebaseAuth.getInstance();
        assert actionBar != null;
        actionBar.hide();
        Spinner spin1=(Spinner) findViewById(R.id.plans_cardview);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plan_months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapter);
        ImageButton back = findViewById(R.id.back);
        ImageButton add = findViewById(R.id.addButton);
        back.setOnClickListener(v -> onBackPressed());
        add.setOnClickListener(v -> {
            EditText plan_details = findViewById(R.id.plan_description);
            EditText plan_fee = findViewById(R.id.plan_fee);
            String time = spin1.getSelectedItem().toString();
            Plans plan = new Plans(plan_details.getText().toString() , time.substring(0,2), plan_fee.getText().toString());
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            mDatabase.child(user).child("Plans").child(plan_details.getText().toString()).setValue(plan);
            Toast.makeText(AddPlan.this, "Plan Added Successfully!" , Toast.LENGTH_LONG).show();
            finish();
        });
    }
}