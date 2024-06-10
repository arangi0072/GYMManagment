package com.arpit.gymmanagment.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.AddMember;
import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.adapter.RecyclerViewAdapter;
import com.arpit.gymmanagment.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView totalMembers = binding.totalMembersNumbers;
        final TextView totalActiveMembers = binding.totalActiveMembersNumber;
        final TextView totalMembersExpiresToday = binding.totalMembersExpiresNumber;
        final FloatingActionButton fab = binding.fabHome;
        final TextView collection = binding.colllection;
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(root.getContext() , AddMember.class);
            startActivity(intent);
        });
        recyclerView = binding.recycleview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getUid();
        CollectionReference docRef = db.collection("Members").document(user).collection("Members");
        Query query = db.collection("Members").document(user).collection("Members");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    totalMembers.setText(String.valueOf(snapshot.getCount()));
                }
            }
        });
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
                        collection.setText(String.valueOf(document.get("collection")));
                    } else {
                        Map<String , Object> coll = new HashMap<>();
                        coll.put("collection" , 0);
                        db.collection("Members").document(user).collection("collections").document(finalDate).set(coll);
                        collection.setText("0");
                    }
                }
            }
        });
        String date = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate now = LocalDate.now();
            date += dtf.format(now);
        }
        Query query1 = db.collection("Members").document(user).collection("Members").whereEqualTo("expiry" , date);
        AggregateQuery countQuery1 = query1.count();
        countQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    totalMembersExpiresToday.setText(String.valueOf(snapshot.getCount()));
                }
            }
        });
        Query query2 = db.collection("Members").document(user).collection("Members").whereGreaterThan("expiry" , date);
        AggregateQuery countQuery2 = query2.count();
        countQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    totalActiveMembers.setText(String.valueOf(snapshot.getCount()));
                }
            }
        });
        docRef.whereEqualTo("expiry" , date)
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
                            recyclerViewAdapter = new RecyclerViewAdapter(root.getContext(), membersArrayList);
                            recyclerView.setAdapter(recyclerViewAdapter);
                        } else {
                            Log.d("data", "Error getting documents: ", task.getException());
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
        final TextView totalMembers = binding.totalMembersNumbers;
        final TextView totalActiveMembers = binding.totalActiveMembersNumber;
        final TextView totalMembersExpiresToday = binding.totalMembersExpiresNumber;
        final FloatingActionButton fab = binding.fabHome;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        CollectionReference docRef = db.collection("Members").document(user).collection("Members");
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(root.getContext() , AddMember.class);
            startActivity(intent);
        });
        Query query = db.collection("Members").document(user).collection("Members");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    totalMembers.setText(String.valueOf(snapshot.getCount()));
                }
            }
        });
        String date = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate now = LocalDate.now();
            date += dtf.format(now);
        }
        Query query1 = db.collection("Members").document(user).collection("Members").whereEqualTo("expiry" , date);
        AggregateQuery countQuery1 = query1.count();
        countQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    totalMembersExpiresToday.setText(String.valueOf(snapshot.getCount()));
                }
            }
        });
        final TextView collection = binding.colllection;
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
                        collection.setText(String.valueOf(document.get("collection")));
                    } else {
                        Map<String , Object> coll = new HashMap<>();
                        coll.put("collection" , 0);
                        db.collection("Members").document(user).collection("collections").document(finalDate).set(coll);
                        collection.setText("0");
                    }
                }
            }
        });
        Query query2 = db.collection("Members").document(user).collection("Members").whereGreaterThan("expiry" , date);
        AggregateQuery countQuery2 = query2.count();
        countQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    totalActiveMembers.setText(String.valueOf(snapshot.getCount()));
                }
            }
        });
        recyclerView = binding.recycleview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        docRef.whereEqualTo("expiry" , date)
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
                            recyclerViewAdapter = new RecyclerViewAdapter(root.getContext(), membersArrayList);
                            recyclerView.setAdapter(recyclerViewAdapter);
                        } else {
                            Log.d("data", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}