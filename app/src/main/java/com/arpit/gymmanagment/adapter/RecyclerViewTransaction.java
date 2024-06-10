package com.arpit.gymmanagment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.gymmanagment.Model.Transaction;
import com.arpit.gymmanagment.R;

import java.util.List;

public class RecyclerViewTransaction extends RecyclerView.Adapter<RecyclerViewTransaction.ViewHolder>{
    private Context context;
    private List<Transaction> list;

    public RecyclerViewTransaction(Context context , List<Transaction> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public @NonNull RecyclerViewTransaction.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTransaction.ViewHolder holder, int position) {
        Transaction transaction = list.get(position);
        String data = transaction.getPAY_DATE() + "   By : " + transaction.getPAYMENT_METHOD() + "\n For : " + transaction.getFOR_REASON();
        holder.textView.setText(data);
        String amount = "+"+transaction.getAMOUNT();
        holder.textView_amount.setText(amount);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textView;
        public TextView textView_amount;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.data_view);
            textView_amount = itemView.findViewById(R.id.transaction_amount);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
        }
    }
}
