package com.arpit.gymmanagment.ui.members;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.R;
import com.arpit.gymmanagment.adapter.RecyclerViewAdapterMember;
import com.arpit.gymmanagment.adapter.RecyclerViewAdapterTransaction;
import com.arpit.gymmanagment.databinding.FragmentMembersBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MembersFragment extends Fragment {

    private FragmentMembersBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterMember recyclerViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMembersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        CollectionReference docRef = db.collection("Members").document(user).collection("Members");
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Members> membersArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Members members = new Members(document.getId() , String.valueOf(data.get("name")), String.valueOf( data.get("Phone")), String.valueOf(data.get("address")) , String.valueOf(data.get("expiry")) , String.valueOf(data.get("plan_details")), String.valueOf(data.get("due_payment")), String.valueOf(data.get("plan_fee")) , (Boolean) data.get("image"));
                                membersArrayList.add(members);
                            }
                            recyclerView = binding.recyclerView;
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                            recyclerViewAdapter = new RecyclerViewAdapterMember(root.getContext(), membersArrayList);
                            recyclerView.setAdapter(recyclerViewAdapter);
                        } else {
                            Log.d("data", "Error getting documents: ", task.getException());
                        }
                    }
                });
        EditText search_text = binding.searchText;
        RadioGroup radioGroup = binding.radioGroup;
        radioGroup.check(R.id.all_members_radio);

        ImageButton search = binding.searchKey;
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.whereEqualTo("Phone" , search_text.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Members> membersArrayList = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> data = document.getData();
                                        Members members = new Members(document.getId() , String.valueOf(data.get("name")), String.valueOf( data.get("Phone")), String.valueOf(data.get("address")) , String.valueOf(data.get("expiry")) , String.valueOf(data.get("plan_details")), String.valueOf(data.get("due_payment")), String.valueOf(data.get("plan_fee")) , (Boolean) data.get("image"));
                                        membersArrayList.add(members);
                                    }
                                    if (membersArrayList.size() == 0){
                                        Toast.makeText(root.getContext(), "No Result Found!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        recyclerViewAdapter = new RecyclerViewAdapterMember(root.getContext(), membersArrayList);
                                        recyclerView.setAdapter(recyclerViewAdapter);
                                    }
                                } else {
                                    Log.d("data", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = root.findViewById(checkedId);
                if (radioButton.getText().toString().equals("All Members")){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    CollectionReference docRef = db.collection("Members").document(user).collection("Members");
                    docRef.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<Members> membersArrayList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> data = document.getData();
                                            Members members = new Members(document.getId() , String.valueOf(data.get("name")), String.valueOf( data.get("Phone")), String.valueOf(data.get("address")) , String.valueOf(data.get("expiry")) , String.valueOf(data.get("plan_details")), String.valueOf(data.get("due_payment")), String.valueOf(data.get("plan_fee")) , (Boolean) data.get("image"));
                                            membersArrayList.add(members);
                                        }
                                        recyclerView = binding.recyclerView;
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                                        recyclerViewAdapter = new RecyclerViewAdapterMember(root.getContext(), membersArrayList);
                                        recyclerView.setAdapter(recyclerViewAdapter);
                                    } else {
                                        Log.d("data", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
                else if (radioButton.getText().toString().equals("Active Members")){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    CollectionReference docRef = db.collection("Members").document(user).collection("Members");
                    String date = "";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                        LocalDate now = LocalDate.now();
                        date += dtf.format(now);
                    }
                    docRef.whereGreaterThanOrEqualTo("expiry" , date)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<Members> membersArrayList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> data = document.getData();
                                            Members members = new Members(document.getId() , String.valueOf(data.get("name")), String.valueOf( data.get("Phone")), String.valueOf(data.get("address")) , String.valueOf(data.get("expiry")) , String.valueOf(data.get("plan_details")), String.valueOf(data.get("due_payment")), String.valueOf(data.get("plan_fee")) , (Boolean) data.get("image"));
                                            membersArrayList.add(members);
                                        }
                                        recyclerView = binding.recyclerView;
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                                        recyclerViewAdapter = new RecyclerViewAdapterMember(root.getContext(), membersArrayList);
                                        recyclerView.setAdapter(recyclerViewAdapter);
                                    } else {
                                        Log.d("data", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
                else if (radioButton.getText().toString().equals("Inactive Members")){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    CollectionReference docRef = db.collection("Members").document(user).collection("Members");
                    String date = "";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                        LocalDate now = LocalDate.now();
                        date += dtf.format(now);
                    }
                    docRef.whereLessThan("expiry" , date)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<Members> membersArrayList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> data = document.getData();
                                            Members members = new Members(document.getId() , String.valueOf(data.get("name")), String.valueOf( data.get("Phone")), String.valueOf(data.get("address")) , String.valueOf(data.get("expiry")) , String.valueOf(data.get("plan_details")), String.valueOf(data.get("due_payment")), String.valueOf(data.get("plan_fee")) , (Boolean) data.get("image"));
                                            membersArrayList.add(members);
                                        }
                                        recyclerView = binding.recyclerView;
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                                        recyclerViewAdapter = new RecyclerViewAdapterMember(root.getContext(), membersArrayList);
                                        recyclerView.setAdapter(recyclerViewAdapter);
                                    } else {
                                        Log.d("data", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onStart(){
        super.onStart();
        View root = binding.getRoot();
        RadioGroup radioGroup = binding.radioGroup;
        radioGroup.check(R.id.all_members_radio);
        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        CollectionReference docRef = db.collection("Members").document(user).collection("Members");
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Members> membersArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Members members = new Members(document.getId() , String.valueOf(data.get("name")), String.valueOf( data.get("Phone")), String.valueOf(data.get("address")) , String.valueOf(data.get("expiry")) , String.valueOf(data.get("plan_details")), String.valueOf(data.get("due_payment")), String.valueOf(data.get("plan_fee")) , (Boolean) data.get("image"));
                                membersArrayList.add(members);
                            }
                            recyclerView = binding.recyclerView;
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                            recyclerViewAdapter = new RecyclerViewAdapterMember(root.getContext(), membersArrayList);
                            recyclerView.setAdapter(recyclerViewAdapter);
                        } else {
                            Log.d("data", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}