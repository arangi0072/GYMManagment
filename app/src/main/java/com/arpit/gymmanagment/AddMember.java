package com.arpit.gymmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.Model.Plans;
import com.arpit.gymmanagment.Model.Transaction;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AddMember extends AppCompatActivity {
    private final int gallery_code = 101;
    OutputStream outputStream;
    private boolean image_represent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        ActivityCompat.requestPermissions(AddMember.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
        ImageButton back = findViewById(R.id.back);
        final String[] final_date_string = {""};
        Spinner spin2=(Spinner) findViewById(R.id.payment_methods);
        ImageButton add = findViewById(R.id.addButton);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        ArrayList<String> plans = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Plans> PlansList = new ArrayList<>();
        Spinner spin1=(Spinner) findViewById(R.id.plans_cardview);
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase.child(user).child("Plans").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddMember.this, android.R.layout.simple_spinner_dropdown_item, plans);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin1.setAdapter(adapter);
                        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(AddMember.this, R.array.payment_method, android.R.layout.simple_spinner_dropdown_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin2.setAdapter(adapter1);
                        String date = "";
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                            LocalDate now = LocalDate.now();
                            date += dtf.format(now);
                        }
                        TextView plan_fee = findViewById(R.id.plan_fees);
                        TextView expire_date = findViewById(R.id.expire_date);
                        TextView due_amount = findViewById(R.id.due_amount);
                        String finalDate = date;
                        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Plans plan1 = PlansList.get(position);
                                String fee = plan1.getFee();
                                plan_fee.setText(fee);
                                due_amount.setText(fee);
                                int expire_date_int = Integer.parseInt(finalDate.substring(4, 6)) + Integer.parseInt(plan1.getTime());
                                if (expire_date_int < 12) {
                                    int final_date_int = Integer.parseInt(finalDate) + Integer.parseInt(plan1.getTime()) * 100;
                                    final_date_string[0] = ("" + final_date_int);
                                    String date_string = final_date_string[0].substring(0, 4) + "-" + final_date_string[0].substring(4, 6) + "-" + final_date_string[0].substring(6, 8);
                                    expire_date.setText(date_string);
                                } else {
                                    int i = Integer.parseInt(finalDate.substring(0, 4)) * 10000;
                                    if (expire_date_int == 12) {
                                        int final_date_int = i + 10100 + Integer.parseInt(finalDate.substring(6, 8));
                                        String final_date_string = String.valueOf(final_date_int);
                                        String date_string = final_date_string.substring(0, 4) + "-" + final_date_string.substring(4, 6) + "-" + final_date_string.substring(6, 8);
                                        expire_date.setText(date_string);
                                    } else {
                                        int mont = (12 - Integer.parseInt(finalDate.substring(4, 6)));
                                        int final_date_int = i + 10000 + (Integer.parseInt(plan1.getTime()) - mont) * 100 + Integer.parseInt(finalDate.substring(6, 8));
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
                        Log.d("firebase", task.getResult().getValue() + " " + PlansList);
                    }
                    else {
                        Toast.makeText(AddMember.this, "First add a Plan!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        ImageButton pick = findViewById(R.id.pick_button);
        TextView plan_fee = findViewById(R.id.plan_fees);
        TextView due_amount = findViewById(R.id.due_amount);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                AddMember.this.startActivityForResult(iGallery , gallery_code);
            }
        });
        EditText paid_amount = findViewById(R.id.paid_amount);
        paid_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String amount = plan_fee.getText().toString();
                String paid = paid_amount.getText().toString();
                if (!paid.equals("")) {
                    if (Integer.parseInt(paid)> Integer.parseInt(amount)) {
                        due_amount.setText("0");
                        paid_amount.setText(amount);
                    }
                    else {
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

        back.setOnClickListener(v -> onBackPressed());
        add.setOnClickListener(v -> {
            EditText name = findViewById(R.id.name);
            EditText phone = findViewById(R.id.phone);
            EditText address = findViewById(R.id.address);
            TextView expire_date = findViewById(R.id.expire_date);
            String final_date = expire_date.getText().toString().substring(0,4) + expire_date.getText().toString().substring(5,7) + expire_date.getText().toString().substring(8,10);
            Members members = new Members(name.getText().toString() , phone.getText().toString() , address.getText().toString() , final_date , spin1.getSelectedItem().toString() , due_amount.getText().toString() , plan_fee.getText().toString() , false);
            Transaction transaction;
            if (paid_amount.getText().toString().equals("")){
                transaction = new Transaction("0" , spin2.getSelectedItem().toString() , spin1.getSelectedItem().toString() + " PLan " + plan_fee.getText().toString());
            }
            else {
                transaction = new Transaction(paid_amount.getText().toString() , spin2.getSelectedItem().toString() , spin1.getSelectedItem().toString() + " PLan " + plan_fee.getText().toString());
            }
            if (phone.getText().toString().length() == 10) {
                ImageView imageView = findViewById(R.id.photo_picked);
                ProgressDialog progressDialog = new ProgressDialog(AddMember.this);
                progressDialog.setTitle("Please Wait !");
                progressDialog.setMessage("Adding Member ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                if (image_represent) {
                    members.setIMAGE(true);
                    Map<String, Object> member = members.toMap();
                    Map<String , Object> tran = transaction.toMap();
                    tran.put("pay_date" , FieldValue.serverTimestamp());
                    db.collection("Members").document(user).collection("Members").add(member).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            db.collection("Members").document(user).collection("Members").document(documentReference.getId()).collection("Transactions").add(tran);
                            imageView.setDrawingCacheEnabled(true);
                            imageView.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream bass = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bass);
                            byte[] data = bass.toByteArray();
                            String filename = documentReference.getId() + ".jpg";
                            StorageReference mountainImagesRef = storageRef.child(user).child(filename);
                            UploadTask uploadTask = mountainImagesRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(AddMember.this, "Member Added To Databases Successfully!!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                } else {
                    Map<String, Object> member = members.toMap();
                    Map<String , Object> tran = transaction.toMap();
                    tran.put("pay_date" , FieldValue.serverTimestamp());
                    String date = "";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate now = LocalDate.now();
                        date += dtf.format(now);
                    }
                    tran.put("date" , date);
                    db.collection("Members").document(user).collection("Members").add(member).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String id = documentReference.getId();
                            db.collection("Members").document(user).collection("Members").document(id).collection("Transactions").add(tran);

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
                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(AddMember.this, "Member Added To Databases Successfully!!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
            else {
                Toast.makeText(this, "Please Enter A Valid Number!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityResult(int request_code , int result_code , @NonNull Intent data) {
        try {
            super.onActivityResult(gallery_code, result_code, data);
            if (result_code == RESULT_OK) {
                if (request_code == gallery_code) {
                    ImageView image = findViewById(R.id.photo_picked);
                    String picturePath =  getRealPathFromURI(data.getData());
                    File imageFile = new File(picturePath);
                    if (imageFile.exists()) {
                        Glide.with(AddMember.this)
                                .load(imageFile)
                                .into(image);
                        image_represent = true; 
                    }
                }
            }
        }
        catch (Exception e){
            Toast.makeText(AddMember.this, "Please use low resolution Image!" , Toast.LENGTH_LONG).show();
        }
    }
    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}