package com.arpit.gymmanagment.ui.transaction;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.adapter.RecyclerViewAdapterTransaction;
import com.arpit.gymmanagment.databinding.FragmentTransactionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class TransactionFragment extends Fragment {
    private RecyclerViewAdapterTransaction recyclerViewAdapter;
    private FragmentTransactionBinding binding;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recylcView;
        ImageButton search = binding.searchKey;
        EditText search_text = binding.searchText;
        recyclerView.setHasFixedSize(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        CollectionReference docRef = db.collection("Members").document(user).collection("Members");
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
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
                                        recyclerViewAdapter = new RecyclerViewAdapterTransaction(root.getContext(), membersArrayList);
                                        recyclerView.setAdapter(recyclerViewAdapter);
                                    }
                                } else {
                                    Log.d("data", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}