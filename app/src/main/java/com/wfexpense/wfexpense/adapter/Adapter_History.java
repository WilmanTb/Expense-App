package com.wfexpense.wfexpense.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wfexpense.wfexpense.R;
import com.wfexpense.wfexpense.model.Balance_Model;

import java.util.ArrayList;

public class Adapter_History extends RecyclerView.Adapter<Adapter_History.ViewHolder> {

    Context context;
    ArrayList<CharSequence> myList;
    ArrayList<Balance_Model> incomeList;
    ArrayList<Balance_Model> expenseList;
    ArrayList<Balance_Model> mergeList;

    public Adapter_History(Context context, ArrayList<CharSequence> myList, ArrayList<Balance_Model> incomeList, ArrayList<Balance_Model> expenseList, ArrayList<Balance_Model> mergeList) {
        this.context = context;
        this.myList = myList;
        this.incomeList = incomeList;
        this.expenseList = expenseList;
        this.mergeList = mergeList;
    }

    @NonNull
    @Override
    public Adapter_History.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_history, parent, false);
        return new Adapter_History.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_History.ViewHolder holder, int position) {
        String tanggalExpense = myList.get(position).toString();
        holder.tanggal_expense.setText(tanggalExpense);
        holder.rc_history.setHasFixedSize(true);
        holder.rc_history.setLayoutManager(new LinearLayoutManager(context));
        mergeList = new ArrayList<>();
        Adapter_List_History adapter_list_history = new Adapter_List_History(context, mergeList);
        holder.rc_history.setAdapter(adapter_list_history);


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
}
