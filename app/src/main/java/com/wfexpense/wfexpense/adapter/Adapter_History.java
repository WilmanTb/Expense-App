package com.wfexpense.wfexpense.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Adapter_History extends RecyclerView.Adapter<Adapter_History.ViewHolder> {

    private Context context;
    private ArrayList<CharSequence> myList;
    private ArrayList<Balance_Model> mergeList;

    public Adapter_History(Context context, ArrayList<CharSequence> myList, ArrayList<Balance_Model> mergeList) {
        this.context = context;
        this.myList = myList;
        this.mergeList = mergeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentDate = getCurrentDate();
        holder.tanggal_expense.setText(myList.get(position).toString());

        Adapter_List_History adapter_list_history = new Adapter_List_History(context, mergeList);
        holder.rc_history.setLayoutManager(new LinearLayoutManager(context));
        holder.rc_history.setAdapter(adapter_list_history);

        DatabaseReference dbBalance = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();

        ValueEventListener balanceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mergeList.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String balanceType = dataSnapshot.getKey();
                        dbBalance.child("Balance").child(balanceType).orderByChild("tanggal").equalTo(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                                        Balance_Model balance_model = expenseSnapshot.getValue(Balance_Model.class);
                                        mergeList.add(balance_model);
                                    }
                                    adapter_list_history.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        dbBalance.child("Balance").child("Expense").addValueEventListener(balanceListener);
        dbBalance.child("Balance").child("Income").addValueEventListener(balanceListener);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rc_history;
        TextView tanggal_expense;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rc_history = itemView.findViewById(R.id.rc_list_expense);
            tanggal_expense = itemView.findViewById(R.id.tanggal_expense);
        }
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }
}
