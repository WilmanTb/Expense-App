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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter_List_Income extends RecyclerView.Adapter<Adapter_List_Income.ViewHolder> {

    Context context;
    ArrayList<CharSequence> myList;
    DatabaseReference dbBalance;

    public Adapter_List_Income(Context context, ArrayList<CharSequence> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public Adapter_List_Income.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_income, parent, false);
        return new Adapter_List_Income.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_List_Income.ViewHolder holder, int position) {
        String tanggalIncome = myList.get(position).toString();
        holder.tanggal_income.setText(tanggalIncome);
        holder.rc_list_income.setHasFixedSize(true);
        holder.rc_list_income.setLayoutManager(new LinearLayoutManager(context));
        ArrayList<Balance_Model> myModel = new ArrayList<>();
        Adapter_List_Data_Income adapter_list_data_income = new Adapter_List_Data_Income(context, myModel);
        holder.rc_list_income.setAdapter(adapter_list_data_income);
        dbBalance = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();
        holder.cv_list_income.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation));

        dbBalance.child("Balance").child("Income").child(tanggalIncome).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sumIncome = 0;
                if (snapshot.exists()) {
                    myModel.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Balance_Model balanceModel = dataSnapshot.getValue(Balance_Model.class);
                        myModel.add(balanceModel);
                        int expenseCost = Integer.parseInt(balanceModel.getHarga());
                        sumIncome += expenseCost;
                        adapter_list_data_income.notifyDataSetChanged();
                    }
                    holder.total_income.setText("Total : " + formatRupiah(Double.parseDouble(String.valueOf(sumIncome))));
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

        TextView tanggal_income, total_income;
        RecyclerView rc_list_income;
        CardView cv_list_income;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tanggal_income = itemView.findViewById(R.id.tanggal_income);
            rc_list_income = itemView.findViewById(R.id.rc_list_income);
            cv_list_income = itemView.findViewById(R.id.cv_list_income);
            total_income = itemView.findViewById(R.id.total_income);
        }
    }

    private String formatRupiah(Double number) {
        Locale locale = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(locale);
        return formatRupiah.format(number);
    }

}