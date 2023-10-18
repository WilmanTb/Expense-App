package com.wfexpense.wfexpense.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wfexpense.wfexpense.R;
import com.wfexpense.wfexpense.model.Balance_Model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter_List_History extends RecyclerView.Adapter<Adapter_List_History.ViewHolder> {

    Context context;
    ArrayList<Balance_Model> myList;

    public Adapter_List_History(Context context, ArrayList<Balance_Model> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public Adapter_List_History.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_history,parent,false);
        return new Adapter_List_History.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_List_History.ViewHolder holder, int position) {
        Balance_Model balance_model = myList.get(position);
        holder.item_cost.setText(formatRupiah(Double.parseDouble(balance_model.getHarga())));
        holder.item_name.setText(balance_model.getNama());
        holder.item_type.setText(balance_model.getJenisTambah());

        String jenisExpense = balance_model.getJenisExpense();
        if (jenisExpense.equals("makanan"))
            holder.item_icon.setImageResource(R.drawable.food);
        if (jenisExpense.equals("minuman"))
            holder.item_icon.setImageResource(R.drawable.drinks);
        if (jenisExpense.equals("jajanan"))
            holder.item_icon.setImageResource(R.drawable.snack);
        if (jenisExpense.equals("pakaian"))
            holder.item_icon.setImageResource(R.drawable.cloth);
        if (jenisExpense.equals("lainnya"))
            holder.item_icon.setImageResource(R.drawable.other);
        if (jenisExpense.equals(""))
            holder.item_icon.setImageResource(R.drawable.salary);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_cost, item_name, item_type;
        ImageView item_icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_cost = itemView.findViewById(R.id.expense_item_cost);
            item_name = itemView.findViewById(R.id.expense_item_name);
            item_type = itemView.findViewById(R.id.item_type);
            item_icon = itemView.findViewById(R.id.expense_item_icon);
        }
    }

    private String formatRupiah(Double number){
        Locale locale = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(locale);
        return formatRupiah.format(number);
    }
}
