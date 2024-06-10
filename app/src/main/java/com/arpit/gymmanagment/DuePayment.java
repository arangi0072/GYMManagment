package com.arpit.gymmanagment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.Model.Transaction;
import com.arpit.gymmanagment.adapter.RecyclerViewTransaction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DuePayment extends AppCompatActivity {
    private RecyclerView recyclerView;

    private boolean image_represent = false;
    private boolean image_updated = false;
    private String id;
    private RecyclerViewTransaction recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_payment);
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Spinner spin2 = findViewById(R.id.payment_methods);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.payment_method,android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adapter1);
        TextView memberName = findViewById(R.id.member_name);
        TextView memberPhone = findViewById(R.id.member_phone);
        TextView memberAddress = findViewById(R.id.member_address);
        TextView memberPlan = findViewById(R.id.member_plan);
        TextView memberDue = findViewById(R.id.member_due_payment);
        TextView memberExpire = findViewById(R.id.plan_expiry_date);
        ImageButton phone_call = findViewById(R.id.phone_call);
        TextView dueAmount = findViewById(R.id.due_amount);
        ImageView image = findViewById(R.id.imageView2);
        recyclerView = findViewById(R.id.recylertran);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Members members = new Members();
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
                        members.setID(id);
                        assert data != null;
                        members.setNAME(String.valueOf(data.get("name")));
                        members.setPHONE(String.valueOf(data.get("Phone")));
                        members.setADDRESS(String.valueOf(data.get("address")));
                        members.setEXPIRY_DATE(String.valueOf(data.get("expiry")));
                        members.setPLAN_DETAILS(String.valueOf(data.get("plan_details")));
                        members.setPLAN_FEE(String.valueOf(data.get("plan_fee")));
                        members.setDUE_AMOUNT(String.valueOf(data.get("due_payment")));
                        members.setIMAGE((boolean) data.get("image"));
                        image_represent = members.isIMAGE();
                        memberName.setText(members.getNAME());
                        memberPhone.setText(members.getPHONE());
                        memberAddress.setText(members.getADDRESS());
                        memberPlan.setText(members.getPLAN_DETAILS());
                        dueAmount.setText(members.getDUE_AMOUNT());
                        memberDue.setText(members.getDUE_AMOUNT());
                        String expire = members.getEXPIRY_DATE().substring(0,4)+"-"+members.getEXPIRY_DATE().substring(4,6)+"-"+members.getEXPIRY_DATE().substring(6,8);
                        memberExpire.setText(expire);
                        phone_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+members.getPHONE()));
                                DuePayment.this.startActivity(callIntent);
                            }
                        });
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
                                    recyclerViewAdapter = new RecyclerViewTransaction(DuePayment.this , transactionArrayList);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                }
                            }
                        });
                        EditText paid_amount = findViewById(R.id.paid_amount);
                        paid_amount.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String amount = memberDue.getText().toString();
                                String paid = paid_amount.getText().toString();
                                if (!paid.equals("")) {
                                    if (Integer.parseInt(paid)>Integer.parseInt(amount)){
                                        dueAmount.setText("0");
                                        paid_amount.setText(amount);
                                    }
                                    else {
                                        int due = Integer.parseInt(amount) - Integer.parseInt(paid);
                                        dueAmount.setText(String.valueOf(due));
                                    }
                                }
                                else {
                                    dueAmount.setText(amount);
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        if (members.isIMAGE()){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference().child(user).child(members.getID() + ".jpg");
                            if (members.isImage_updated()) {
                                image_updated = true;
                                Glide.with(DuePayment.this)
                                        .load(storageRef)
                                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                        .circleCrop()
                                        .into(image);
                            }
                            else {
                                Glide.with(DuePayment.this)
                                        .load(storageRef)
                                        .circleCrop()
                                        .into(image);
                            }
                        }
                        Button pay = findViewById(R.id.payButton);
                        pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ProgressDialog progressDialog = new ProgressDialog(DuePayment.this);
                                progressDialog.setTitle("Please Wait !");
                                progressDialog.setMessage("Recharging Member ...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                docRef.update("due_payment" , dueAmount.getText().toString());
                                Transaction transaction = new Transaction();
                                if (paid_amount.getText().toString().equals("")) {
                                    transaction.setAMOUNT("0");
                                }
                                else {
                                    transaction.setAMOUNT(paid_amount.getText().toString());
                                }
                                transaction.setPAYMENT_METHOD(spin2.getSelectedItem().toString());
                                transaction.setFOR_REASON("Due " + members.getDUE_AMOUNT());
                                Map<String, Object> tran = transaction.toMap();
                                tran.put("pay_date" , FieldValue.serverTimestamp());
                                String date = "";
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    LocalDate now = LocalDate.now();
                                    date += dtf.format(now);
                                }
                                tran.put("date" , date);
                                docRef.collection("Transactions").add(tran);
                                String date1 = "";
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");
                                    LocalDate now = LocalDate.now();
                                    date1 += dtf.format(now);
                                }
                                String finalDate = date1;
                                db.collection("Members").document(user).collection("collections").document(date1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                DocumentReference doc = db.collection("Members").document(user).collection("collections").document(finalDate);
                                                int collections = Integer.parseInt(String.valueOf(document.get("collection")));
                                                int collection = collections + Integer.parseInt(paid_amount.getText().toString());
                                                doc.update("collection" , collection);
                                            } else {
                                                Map<String , Object> coll = new HashMap<>();
                                                coll.put("collection" , 0);
                                                db.collection("Members").document(user).collection("collections").document(finalDate).set(coll);
                                            }
                                        }
                                    }
                                });
                                progressDialog.dismiss();
                                Toast.makeText(DuePayment.this, "Paid! " + paid_amount.getText().toString(), Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }
                }
            }
        });
        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void showImage(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box);
        ImageView popupImage = dialog.findViewById(R.id.imageView);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        if (image_represent){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(user).child(id + ".jpg");
            if (image_updated) {
                Glide.with(DuePayment.this)
                        .load(storageRef)
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .circleCrop()
                        .into(popupImage);
            }
            else {
                Glide.with(DuePayment.this)
                        .load(storageRef)
                        .circleCrop()
                        .into(popupImage);
            }
        }
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}