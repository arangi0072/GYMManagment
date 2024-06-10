package com.arpit.gymmanagment.ui.recharge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.R;
import com.arpit.gymmanagment.adapter.RecyclerViewAdapter;
import com.arpit.gymmanagment.adapter.RecyclerViewAdapterTransaction;
import com.arpit.gymmanagment.databinding.FragmentRechargeBinding;
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

public class RechargeFragment extends Fragment {

    private FragmentRechargeBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRechargeBinding.inflate(inflater , container , false);
        View root = binding.getRoot();
        recyclerView = binding.recycleView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
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
        TextView search_text = root.findViewById(R.id.searchText);
        ImageButton search = root.findViewById(R.id.search_key);
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
                                        recyclerViewAdapter = new RecyclerViewAdapter(root.getContext(), membersArrayList);
                                        recyclerView.setAdapter(recyclerViewAdapter);
                                    }
                                } else {
                                    Log.d("data", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        docRef.whereLessThanOrEqualTo("expiry" , date)
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
    public  void onStart(){
        super.onStart();
        View root = binding.getRoot();
        recyclerView = binding.recycleView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
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
        docRef.whereLessThanOrEqualTo("expiry" , date)
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