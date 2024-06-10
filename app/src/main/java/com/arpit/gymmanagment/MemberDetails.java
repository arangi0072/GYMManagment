package com.arpit.gymmanagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.arpit.gymmanagment.Model.Members;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

public class MemberDetails extends AppCompatActivity {
    private String id;
    private boolean image_represent = false;
    private boolean image_updated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        TextView memberName = findViewById(R.id.member_name);
        TextView memberPhone = findViewById(R.id.member_phone);
        TextView memberAddress = findViewById(R.id.member_address);
        TextView memberPlan = findViewById(R.id.member_plan);
        ImageView image = findViewById(R.id.imageView2);
        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button edit = findViewById(R.id.editButton);
        Button recharge = findViewById(R.id.rechargeButton1);
        TextView memberExpire = findViewById(R.id.plan_expiry_date);
        ImageButton phone_call = findViewById(R.id.phone_call);
        TextView dueAmount = findViewById(R.id.member_due_payment);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Members members = new Members();
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
                        dueAmount.setText(members.getPLAN_FEE());
                        String expire = members.getEXPIRY_DATE().substring(0,4)+"-"+members.getEXPIRY_DATE().substring(4,6)+"-"+members.getEXPIRY_DATE().substring(6,8);
                        memberExpire.setText(expire);
                        phone_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+members.getPHONE()));
                                MemberDetails.this.startActivity(callIntent);
                            }
                        });
                        if (members.isIMAGE()){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference().child(user).child(members.getID() + ".jpg");
                            if (members.isImage_updated()) {
                                image_updated = true;
                                Glide.with(MemberDetails.this)
                                        .load(storageRef)
                                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                        .circleCrop()
                                        .into(image);
                            }
                            else {
                                Glide.with(MemberDetails.this)
                                        .load(storageRef)
                                        .circleCrop()
                                        .into(image);
                            }
                        }
                        recharge.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(MemberDetails.this , RechargeActivity.class);
                                intent1.putExtra("id" , id);
                                MemberDetails.this.startActivity(intent1);
                            }
                        });
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(MemberDetails.this , EditDetails.class);
                                intent1.putExtra("id" , id);
                                MemberDetails.this.startActivity(intent1);
                            }
                        });
                        FloatingActionButton fab = findViewById(R.id.delete_fab);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MemberDetails.this);
                                builder1.setTitle("Delete Member");
                                builder1.setMessage("Do You What To Delete This Member?");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                ProgressDialog progressDialog = new ProgressDialog(MemberDetails.this);
                                                progressDialog.setTitle("Please Wait !");
                                                progressDialog.setMessage("Deleting Member ...");
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();
                                                docRef.delete();
                                                if (members.isIMAGE()){
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                                    StorageReference storageRef = storage.getReference().child(user).child(members.getID() + ".jpg");
                                                    storageRef.delete();
                                                }
                                                dialog.cancel();
                                                progressDialog.dismiss();
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
                    }
                }
            }
        });
    }
    @Override
    public void onRestart(){
        super.onRestart();
        TextView memberName = findViewById(R.id.member_name);
        TextView memberPhone = findViewById(R.id.member_phone);
        TextView memberAddress = findViewById(R.id.member_address);
        TextView memberPlan = findViewById(R.id.member_plan);
        ImageView image = findViewById(R.id.imageView2);
        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button edit = findViewById(R.id.editButton);
        TextView memberExpire = findViewById(R.id.plan_expiry_date);
        ImageButton phone_call = findViewById(R.id.phone_call);
        TextView dueAmount = findViewById(R.id.member_due_payment);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Members members = new Members();
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
                        if (members.isIMAGE()){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference().child(user).child(members.getID() + ".jpg");
                            Glide.with(MemberDetails.this)
                                    .load(storageRef)
                                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                    .circleCrop()
                                    .into(image);
                        }
                        memberName.setText(members.getNAME());
                        memberPhone.setText(members.getPHONE());
                        memberAddress.setText(members.getADDRESS());
                        memberPlan.setText(members.getPLAN_DETAILS());
                        dueAmount.setText(members.getPLAN_FEE());
                        String expire = members.getEXPIRY_DATE().substring(0,4)+"-"+members.getEXPIRY_DATE().substring(4,6)+"-"+members.getEXPIRY_DATE().substring(6,8);
                        memberExpire.setText(expire);
                        phone_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+members.getPHONE()));
                                MemberDetails.this.startActivity(callIntent);
                            }
                        });
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(MemberDetails.this , EditDetails.class);
                                intent1.putExtra("id" , id);
                                MemberDetails.this.startActivity(intent1);
                            }
                        });
                    }
                }
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
                Glide.with(MemberDetails.this)
                        .load(storageRef)
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .into(popupImage);
            }
            else {
                Glide.with(MemberDetails.this)
                        .load(storageRef)
                        .into(popupImage);
            }
        }
        else {
            popupImage.setImageResource(R.drawable.baseline_person_24);
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