package com.arpit.gymmanagment.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.R;
import com.arpit.gymmanagment.RechargeActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Members> memberList;

    public RecyclerViewAdapter(Context context, List<Members> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Members member = memberList.get(position);
        String expire = member.getEXPIRY_DATE().substring(0,4) +"-"+member.getEXPIRY_DATE().substring(4,6)+"-"+member.getEXPIRY_DATE().substring(6,8);
        holder.memberName.setText(member.getNAME());
        holder.memberPhone.setText(member.getPHONE());
        holder.memberAddress.setText(member.getADDRESS());
        holder.memberPlan.setText(member.getPLAN_DETAILS());
        holder.phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+member.getPHONE()));
                context.startActivity(callIntent);
            }
        });
        if (member.isIMAGE()){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(user).child(member.getID() + ".jpg");
            if (member.isImage_updated()) {
                Glide.with(context)
                        .load(storageRef)
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .circleCrop()
                        .into(holder.iconButton);
            }
            else {
                Glide.with(context)
                        .load(storageRef)
                        .circleCrop()
                        .into(holder.iconButton);
            }
        }
        holder.memberPlanFee.setText(member.getPLAN_FEE());
        holder.planExpiry.setText(expire);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView memberName;
        public TextView memberPhone;
        public TextView memberAddress;
        public TextView memberPlan;
        public TextView memberPlanFee;
        public TextView planExpiry;
        public ImageView iconButton;
        public ImageButton phone_call;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            memberName = itemView.findViewById(R.id.member_name);
            memberPhone = itemView.findViewById(R.id.member_phone);
            memberAddress = itemView.findViewById(R.id.member_address);
            memberPlan = itemView.findViewById(R.id.member_plan);
            memberPlanFee = itemView.findViewById(R.id.due_amount);
            planExpiry = itemView.findViewById(R.id.plan_expiry_date);
            iconButton = itemView.findViewById(R.id.icon_button);
            phone_call = itemView.findViewById(R.id.phone_call);
            iconButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            Members members = memberList.get(position);
            Intent intent = new Intent(context , RechargeActivity.class);
            intent.putExtra("id", members.getID());
            context.startActivity(intent);

        }
    }
}
