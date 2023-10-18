package com.wfexpense.wfexpense.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wfexpense.wfexpense.Global_Data;
import com.wfexpense.wfexpense.R;
import com.wfexpense.wfexpense.model.Balance_Model;

import java.util.ArrayList;

public class Adapter_List_Expense extends RecyclerView.Adapter<Adapter_List_Expense.ViewHolder> {

    Context context;
    ArrayList<CharSequence> myList;
    DatabaseReference dbBalance;

    public Adapter_List_Expense(Context context, ArrayList<CharSequence> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public Adapter_List_Expense.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_expense, parent, false);
        return new Adapter_List_Expense.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_List_Expense.ViewHolder holder, int position) {
        String tanggalExpense = myList.get(position).toString();
        holder.tanggal_expense.setText(tanggalExpense);
        holder.rc_list_expense.setHasFixedSize(true);
        holder.rc_list_expense.setLayoutManager(new LinearLayoutManager(context));
        ArrayList<Balance_Model> myModel = new ArrayList<>();
        Adapter_List_Data_Expense adapter_list_data_expense = new Adapter_List_Data_Expense(context, myModel);
        holder.rc_list_expense.setAdapter(adapter_list_data_expense);
        dbBalance = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();
        holder.cv_list_expense.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation));

        dbBalance.child("Balance").child("Expense").child(tanggalExpense).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myModel.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Balance_Model balanceModel = dataSnapshot.getValue(Balance_Model.class);
                        myModel.add(balanceModel);
                    }
                    adapter_list_data_expense.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tanggal_expense;
        RecyclerView rc_list_expense;
        CardView cv_list_expense;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tanggal_expense = itemView.findViewById(R.id.tanggal_expense);
            rc_list_expense = itemView.findViewById(R.id.rc_list_expense);
            cv_list_expense = itemView.findViewById(R.id.cv_list_expense);
        }
    }

}