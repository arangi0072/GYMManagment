package com.arpit.gymmanagment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Transaction;
import com.arpit.gymmanagment.adapter.RecyclerViewTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Transactions extends AppCompatActivity {
    private RecyclerViewTransaction recyclerViewAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView name = findViewById(R.id.member_name_tran);
        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DocumentReference docRef = db.collection("Members").document(user).collection("Members").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String  , Object> data = document.getData();
                        name.setText(String.valueOf(data.get("name")));
                        docRef.collection("Transactions").orderBy("pay_date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                }
                                else {
                                    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> data = document.getData();
                                        Transaction transaction = new Transaction(String.valueOf(data.get("date")), String.valueOf(data.get("amount")) , String.valueOf(data.get("payment_method")) , String.valueOf(data.get("reason")));
                                        transactionArrayList.add(transaction);
                                    }
                                    recyclerViewAdapter = new RecyclerViewTransaction(Transactions.this , transactionArrayList);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                }
                            }
                        });
                    }
                }
            }
        });
        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}