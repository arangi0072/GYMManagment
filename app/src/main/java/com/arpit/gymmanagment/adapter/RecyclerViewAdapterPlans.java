package com.arpit.gymmanagment.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Plans;
import com.arpit.gymmanagment.PlanDetails;
import com.arpit.gymmanagment.R;

import java.util.List;

public class RecyclerViewAdapterPlans  extends RecyclerView.Adapter<RecyclerViewAdapterPlans.ViewHolder> {
    private Context context;
    private List<Plans> planList;

    public RecyclerViewAdapterPlans(Context context, List<Plans> plansList) {
        this.context = context;
        this.planList = plansList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plans_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plans plans = planList.get(position);
        String plan_time = plans.getTime()+" Months";
        holder.plan_details.setText(plans.getPlan_details());
        holder.plan_fee.setText(plans.getFee());
        holder.plan_timePeriod.setText(plan_time);
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView plan_details;
        public TextView plan_fee;
        public TextView plan_timePeriod;
        public ImageView iconButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            plan_details = itemView.findViewById(R.id.plans_cardview);
            plan_fee = itemView.findViewById(R.id.plan_fee);
            plan_timePeriod = itemView.findViewById(R.id.plan_timePeriod);
            iconButton = itemView.findViewById(R.id.icon_button);
            iconButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            Intent intent = new Intent(context , PlanDetails.class);
            Plans plan  = planList.get(position);
            intent.putExtra("plan" , plan.getPlan_details());
            context.startActivity(intent);
        }
    }
}
