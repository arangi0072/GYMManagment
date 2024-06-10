package com.arpit.gymmanagment.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.DuePayment;
import com.arpit.gymmanagment.Model.Members;
import com.arpit.gymmanagment.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

public class RecyclerViewDue extends RecyclerView.Adapter<RecyclerViewDue.ViewHolder> {
    private Context context;
    private List<Members> membersList;

    public RecyclerViewDue(Context context, List<Members> membersList) {
        this.context = context;
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public RecyclerViewDue.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewDue.ViewHolder holder, int position) {
        Members member = membersList.get(position);
        String expire = member.getEXPIRY_DATE().substring(0,4) +"-"+member.getEXPIRY_DATE().substring(4,6)+"-"+member.getEXPIRY_DATE().substring(6,8);
        holder.memberName.setText(member.getNAME());
        holder.memberPhone.setText(member.getPHONE());
        holder.memberAddress.setText(member.getADDRESS());
        holder.memberPlan.setText(member.getPLAN_DETAILS());
        String due_string = "Due Amount:";
        holder.due_text.setText(due_string);
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
        holder.memberPlanFee.setText(member.getDUE_AMOUNT());
        holder.memberPlanFee.setTextColor(Color.RED);
        holder.planExpiry.setText(expire);
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView memberName;
        public TextView memberPhone;
        public TextView memberAddress;
        public TextView memberPlan;
        public TextView memberPlanFee;
        public TextView planExpiry;
        public TextView due_text;
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
            due_text = itemView.findViewById(R.id.member_plan_fee_text_view);
            iconButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            Members members = membersList.get(position);
            Intent intent = new Intent(context , DuePayment.class);
            intent.putExtra("id" , members.getID());
            context.startActivity(intent);
        }
    }
}
