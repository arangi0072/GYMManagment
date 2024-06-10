package com.arpit.gymmanagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.Model.Plans;
import com.arpit.gymmanagment.Model.Transaction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RechargeActivity extends AppCompatActivity {
    private String  id;
    private boolean image_represent = false;
    private boolean isImage_updated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        try {
            ActionBar actionBar = this.getSupportActionBar();
            assert actionBar != null;
            actionBar.hide();
            Spinner spin2 = findViewById(R.id.payment_methods);
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.payment_method, android.R.layout.simple_spinner_dropdown_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin2.setAdapter(adapter1);
            TextView memberName = findViewById(R.id.member_name);
            TextView memberPhone = findViewById(R.id.member_phone);
            EditText paid_amount = findViewById(R.id.paid_amount);
            TextView memberAddress = findViewById(R.id.member_address);
            TextView memberPlan = findViewById(R.id.member_plan);
            ImageView image = findViewById(R.id.imageView2);
            TextView memberExpire = findViewById(R.id.plan_expiry_date);
            TextView memberExpireDate = findViewById(R.id.member_expire_date);
            ImageButton phone_call = findViewById(R.id.phone_call);

            TextView dueAmount = findViewById(R.id.member_due_payment);
            TextView plan_fee = findViewById(R.id.plan_fees);
            Intent intent = getIntent();
            id = intent.getStringExtra("id");
            Members members = new Members();
            TextView due_amount = findViewById(R.id.due_amount);
            ArrayList<String> plans = new ArrayList<>();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            List<Plans> PlansList = new ArrayList<>();
            final String[] final_date_string = {""};
            Spinner spin1 = (Spinner) findViewById(R.id.plans_cardview);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            DocumentReference docRef = db.collection("Members").document(user).collection("Members").document(id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
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
                            memberName.setText(members.getNAME());
                            memberPhone.setText(members.getPHONE());
                            memberAddress.setText(members.getADDRESS());
                            memberPlan.setText(members.getPLAN_DETAILS());
                            dueAmount.setText(members.getDUE_AMOUNT());
                            String expire = members.getEXPIRY_DATE().substring(0, 4) + "-" + members.getEXPIRY_DATE().substring(4, 6) + "-" + members.getEXPIRY_DATE().substring(6, 8);
                            memberExpire.setText(expire);
                            isImage_updated = members.isImage_updated();
                            String date = "";
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                                LocalDate now = LocalDate.now();
                                date += dtf.format(now);
                            }
                            String finalDate = date;
                            mDatabase.child(user).child("Plans").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    } else {
                                        HashMap object = (HashMap) task.getResult().getValue();
                                        if (object != null) {
                                            Set Plans = object.keySet();
                                            for (Object s :
                                                    Plans) {
                                                HashMap data = (HashMap) object.get(s);
                                                assert data != null;
                                                String plan_details = (String) data.get("plan_details");
                                                String fee = (String) data.get("fee");
                                                String time = (String) data.get("time");
                                                Plans plan = new Plans(plan_details, time, fee);
                                                PlansList.add(plan);
                                                Log.d("firebase", plan.getPlan_details());
                                            }
                                            for (Plans item : PlansList) {
                                                plans.add(item.getPlan_details());
                                            }
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(RechargeActivity.this, android.R.layout.simple_spinner_dropdown_item, plans);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spin1.setAdapter(adapter);
                                            TextView plan_fee = findViewById(R.id.plan_fees);
                                            TextView expire_date = findViewById(R.id.member_expire_date);
                                            TextView due_amount = findViewById(R.id.due_amount);
                                            spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    Plans plan1 = PlansList.get(position);
                                                    String fee = plan1.getFee();
                                                    plan_fee.setText(fee);
                                                    due_amount.setText(fee);
                                                    String amount = String.valueOf(Integer.parseInt(plan_fee.getText().toString()) + Integer.parseInt(members.getDUE_AMOUNT()));
                                                    due_amount.setText(amount);
                                                    paid_amount.addTextChangedListener(new TextWatcher() {
                                                        @Override
                                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                        }

                                                        @Override
                                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                            String amount = String.valueOf(Integer.parseInt(plan_fee.getText().toString()) + Integer.parseInt(members.getDUE_AMOUNT()));
                                                            String paid = paid_amount.getText().toString();
                                                            if (!paid.equals("")) {
                                                                if (Integer.parseInt(paid) > Integer.parseInt(amount)) {
                                                                    due_amount.setText("0");
                                                                    paid_amount.setText(amount);
                                                                } else {
                                                                    int due = Integer.parseInt(amount) - Integer.parseInt(paid);
                                                                    due_amount.setText(String.valueOf(due));
                                                                }
                                                            } else {
                                                                due_amount.setText(amount);
                                                            }
                                                        }

                                                        @Override
                                                        public void afterTextChanged(Editable s) {

                                                        }
                                                    });
                                                    String finalDate1 = "";
                                                    if (Integer.parseInt(members.getEXPIRY_DATE()) > Integer.parseInt(finalDate)) {
                                                        finalDate1 = members.getEXPIRY_DATE();
                                                    } else {
                                                        finalDate1 = finalDate;
                                                    }
                                                    int expire_date_int = Integer.parseInt(finalDate1.substring(4, 6)) + Integer.parseInt(plan1.getTime());
                                                    if (expire_date_int < 12) {
                                                        int final_date_int = Integer.parseInt(finalDate1) + Integer.parseInt(plan1.getTime()) * 100;
                                                        final_date_string[0] = ("" + final_date_int);
                                                        String date_string = final_date_string[0].substring(0, 4) + "-" + final_date_string[0].substring(4, 6) + "-" + final_date_string[0].substring(6, 8);
                                                        expire_date.setText(date_string);
                                                    } else {
                                                        int i = Integer.parseInt(finalDate1.substring(0, 4)) * 10000;
                                                        if (expire_date_int == 12) {
                                                            int final_date_int = i + 10100 + Integer.parseInt(finalDate1.substring(6, 8));
                                                            String final_date_string = String.valueOf(final_date_int);
                                                            String date_string = final_date_string.substring(0, 4) + "-" + final_date_string.substring(4, 6) + "-" + final_date_string.substring(6, 8);
                                                            expire_date.setText(date_string);
                                                        } else {
                                                            int mont = (12 - Integer.parseInt(finalDate1.substring(4, 6)));
                                                            int final_date_int = i + 10000 + (Integer.parseInt(plan1.getTime()) - mont) * 100 + Integer.parseInt(finalDate1.substring(6, 8));
                                                            String final_date_string = String.valueOf(final_date_int);
                                                            String date_string = final_date_string.substring(0, 4) + "-" + final_date_string.substring(4, 6) + "-" + final_date_string.substring(6, 8);
                                                            expire_date.setText(date_string);
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {
                                                }
                                            });
                                            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(RechargeActivity.this, R.array.payment_method, android.R.layout.simple_spinner_dropdown_item);
                                            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spin2.setAdapter(adapter1);
                                        }
                                        else {
                                            Toast.makeText(RechargeActivity.this, "First add a Plan!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            phone_call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + members.getPHONE()));
                                    RechargeActivity.this.startActivity(callIntent);
                                }
                            });
                            if (members.isIMAGE()) {
                                image_represent = true;
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference().child(user).child(members.getID() + ".jpg");
                                if (members.isImage_updated()) {
                                    Glide.with(RechargeActivity.this)
                                            .load(storageRef)
                                            .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                            .circleCrop()
                                            .into(image);
                                } else {
                                    Glide.with(RechargeActivity.this)
                                            .load(storageRef)
                                            .circleCrop()
                                            .into(image);
                                }
                            }

                        }
                    }
                }
            });
            ImageButton back = findViewById(R.id.backButton);
            back.setOnClickListener(v -> onBackPressed());
            Button recharge = findViewById(R.id.rechargeButton);
            recharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RechargeActivity.this);
                    builder1.setTitle("Recharge");
                    builder1.setMessage("Recharge ~ " + spin1.getSelectedItem().toString() + "?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ProgressDialog progressDialog = new ProgressDialog(RechargeActivity.this);
                                    progressDialog.setTitle("Please Wait !");
                                    progressDialog.setMessage("Recharging Member ...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    docRef.update("plan_details", spin1.getSelectedItem().toString());
                                    docRef.update("plan_fee", plan_fee.getText().toString());
                                    String expiry_date = memberExpireDate.getText().toString().substring(0, 4) + memberExpireDate.getText().toString().substring(5, 7) + memberExpireDate.getText().toString().substring(8, 10);
                                    docRef.update("expiry", expiry_date);
                                    docRef.update("due_payment", due_amount.getText().toString());
                                    Transaction transaction = new Transaction();
                                    if (paid_amount.getText().toString().equals("")) {
                                        transaction.setAMOUNT("0");
                                    } else {
                                        transaction.setAMOUNT(paid_amount.getText().toString());
                                    }
                                    transaction.setPAYMENT_METHOD(spin2.getSelectedItem().toString());
                                    transaction.setFOR_REASON(spin1.getSelectedItem().toString() + " Plan " + plan_fee.getText().toString() + " & Due " + members.getDUE_AMOUNT());
                                    Map<String, Object> tran = transaction.toMap();
                                    tran.put("pay_date", FieldValue.serverTimestamp());
                                    String date = "";
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        LocalDate now = LocalDate.now();
                                        date += dtf.format(now);
                                    }
                                    tran.put("date", date);
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
                                    Toast.makeText(RechargeActivity.this, "Recharge Done!", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
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
            if (isImage_updated) {
                Glide.with(RechargeActivity.this)
                        .load(storageRef)
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .circleCrop()
                        .into(popupImage);
            }
            else {
                Glide.with(RechargeActivity.this)
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