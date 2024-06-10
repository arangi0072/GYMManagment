package com.arpit.gymmanagment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.arpit.gymmanagment.Model.Members;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.Objects;

public class EditDetails extends AppCompatActivity {
    private final int gallery_code = 101;
    private String id;
    private boolean representImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        ImageView image = findViewById(R.id.imageView2);
        EditText name = findViewById(R.id.name);
        EditText phone1 = findViewById(R.id.phone);
        EditText address = findViewById(R.id.address);
        ImageButton back = findViewById(R.id.backButton) ;
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
                        name.setText(members.getNAME());
                        phone1.setText(members.getPHONE());
                        address.setText(members.getADDRESS());
                        if (members.isIMAGE()){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference().child(user).child(members.getID() + ".jpg");
                            if (members.isImage_updated()) {
                                Glide.with(EditDetails.this)
                                        .load(storageRef)
                                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                        .circleCrop()
                                        .into(image);
                            }
                            else {
                                Glide.with(EditDetails.this)
                                        .load(storageRef)
                                        .circleCrop()
                                        .into(image);
                            }
                        }
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageButton pick = findViewById(R.id.pick_button);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                EditDetails.this.startActivityForResult(iGallery , gallery_code);
            }
        });
        Button save = findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(EditDetails.this);
                progressDialog.setTitle("Please Wait !");
                progressDialog.setMessage("Updating Member ...");
                progressDialog.show();
                if (representImage) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child(user).child(members.getID() + ".jpg");
                    if (members.isIMAGE()) {
                        storageRef.delete();
                        ImageView imageView = findViewById(R.id.imageView2);
                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream bass = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bass);
                        byte[] data = bass.toByteArray();
                        StorageReference mountainImagesRef = storage.getReference().child(user).child(id+".jpg");
                        UploadTask uploadTask = mountainImagesRef.putBytes(data);
                        docRef.update("image_updated" , true);
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
                    }
                    else {
                        ImageView imageView = findViewById(R.id.imageView2);
                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream bass = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bass);
                        byte[] data = bass.toByteArray();
                        StorageReference mountainImagesRef = storage.getReference().child(user).child(id+".jpg");
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
                    }
                }
                docRef.update("name" , name.getText().toString());
                docRef.update("image" , true);
                docRef.update("Phone" , phone1.getText().toString());
                docRef.update("address" , address.getText().toString());
                progressDialog.dismiss();
                Toast.makeText(EditDetails.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }
    @Override
    public void onActivityResult(int request_code , int result_code , @NonNull Intent data) {
        try {
            super.onActivityResult(gallery_code, result_code, data);
            if (result_code == RESULT_OK) {
                if (request_code == gallery_code) {
                    ImageView image = findViewById(R.id.imageView2);
                    String picturePath =  getRealPathFromURI(data.getData());
                    File imageFile = new File(picturePath);
                    if (imageFile.exists()) {
                        Glide.with(EditDetails.this)
                                .load(imageFile)
                                .into(image);
                    }
                    representImage = true;
                }
            }
        }
        catch (Exception e){
            Toast.makeText(EditDetails.this, "Please use low resolution Image!" , Toast.LENGTH_LONG).show();
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