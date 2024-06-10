package com.arpit.gymmanagment.ui.plans;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.AddPlan;
import com.arpit.gymmanagment.Model.Plans;
import com.arpit.gymmanagment.adapter.RecyclerViewAdapterPlans;
import com.arpit.gymmanagment.databinding.FragmentPlansBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PlansFragment extends Fragment {

    private FragmentPlansBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterPlans recyclerViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlansBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recycler;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        ArrayList<Plans> plansList = new ArrayList<>();
        FloatingActionButton fab = binding.addPlan;
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(root.getContext() , AddPlan.class);
            startActivity(intent);
        });
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        List<Plans> PlansList = new ArrayList<>();
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
                    }

                    plansList.addAll(PlansList);
                    recyclerViewAdapter = new RecyclerViewAdapterPlans(root.getContext(), plansList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    Log.d("firebase", task.getResult().getValue() + " " + PlansList);
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
        recyclerView = binding.recycler;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        ArrayList<Plans> plansList = new ArrayList<>();
        FloatingActionButton fab = binding.addPlan;
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(root.getContext() , AddPlan.class);
            startActivity(intent);
        });
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        List<Plans> PlansList = new ArrayList<>();
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
                    }
                    plansList.addAll(PlansList);
                    Log.d("firebase", task.getResult().getValue() + " " + PlansList);
                    recyclerView.removeAllViewsInLayout();
                    recyclerViewAdapter = new RecyclerViewAdapterPlans(root.getContext(), plansList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            }
        });
    }


}