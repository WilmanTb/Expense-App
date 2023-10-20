package com.wfexpense.wfexpense.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wfexpense.wfexpense.CustomTextWatcher;
import com.wfexpense.wfexpense.Global_Data;
import com.wfexpense.wfexpense.R;
import com.wfexpense.wfexpense.model.Balance_Model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter_List_Data_Expense extends RecyclerView.Adapter<Adapter_List_Data_Expense.ViewHolder> {

    Context context;
    ArrayList<Balance_Model> myList;
    DatabaseReference dbExpense = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();

    public Adapter_List_Data_Expense(Context context, ArrayList<Balance_Model> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public Adapter_List_Data_Expense.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_data_expense, parent, false);
        return new Adapter_List_Data_Expense.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_List_Data_Expense.ViewHolder holder, int position) {
        DatabaseReference dbExpense = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();
        Balance_Model balance_model = myList.get(position);
        holder.expense_item_name.setText(balance_model.getNama());
        holder.expense_item_cost.setText(formatRupiah(Double.parseDouble(balance_model.getHarga())));
        String jenisExpense = balance_model.getJenisExpense();
        if (jenisExpense.equals("makanan"))
            holder.expense_item_icon.setImageResource(R.drawable.food);
        if (jenisExpense.equals("minuman"))
            holder.expense_item_icon.setImageResource(R.drawable.drinks);
        if (jenisExpense.equals("jajanan"))
            holder.expense_item_icon.setImageResource(R.drawable.snack);
        if (jenisExpense.equals("pakaian"))
            holder.expense_item_icon.setImageResource(R.drawable.cloth);
        if (jenisExpense.equals("lainnya"))
            holder.expense_item_icon.setImageResource(R.drawable.other);

        holder.edit_expense_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_item_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText et_expense_name = dialog.findViewById(R.id.et_nama_pengeluaran);
                EditText et_expense_total = dialog.findViewById(R.id.et_jumlah_pengeluaran);
                Button btn_simpan = dialog.findViewById(R.id.btn_simpan);
                AppCompatButton btn_batal = dialog.findViewById(R.id.btn_batal);

                et_expense_name.setText(balance_model.getNama());
                et_expense_total.setText(balance_model.getHarga());
                et_expense_total.addTextChangedListener(new CustomTextWatcher(et_expense_total));

                btn_batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_simpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String expenseName = et_expense_name.getText().toString();
                        String expenseTotal = et_expense_total.getText().toString();
                        String cleanText = expenseTotal.replace(".", "");
                        if (expenseName.isEmpty() || expenseTotal.isEmpty())
                            Toast.makeText(context, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        else {
                            dbExpense.child("Balance").child("Expense").child(balance_model.getTanggal()).child(balance_model.getId()).child("nama").setValue(expenseName);
                            dbExpense.child("Balance").child("Expense").child(balance_model.getTanggal()).child(balance_model.getId()).child("harga").setValue(cleanText);
                            Toast.makeText(context, "Yay! Data nya berhasil diubah ;)", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        holder.remove_expense_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setMessage("Yakin menghapus item ?")
                        .setPositiveButton("Hapus", null)
                        .setNegativeButton("Batal", null)
                        .show();
                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbExpense.child("Balance").child("Expense").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String key = dataSnapshot.getKey();
                                    dbExpense.child("Balance").child("Expense").child(key).child(myList.get(holder.getAdapterPosition()).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                dbExpense.child("Balance").child("Expense").child(key).child(myList.get(holder.getAdapterPosition()).getId()).removeValue();
                                                alertDialog.dismiss();
                                                Toast.makeText(context, "Item berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView expense_item_icon, edit_expense_item, remove_expense_item;
        TextView expense_item_name, expense_item_cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            expense_item_icon = itemView.findViewById(R.id.expense_item_icon);
            expense_item_name = itemView.findViewById(R.id.expense_item_name);
            expense_item_cost = itemView.findViewById(R.id.expense_item_cost);
            edit_expense_item = itemView.findViewById(R.id.edit_expense_item);
            remove_expense_item = itemView.findViewById(R.id.remove_expense_item);
        }
    }

    private String formatRupiah(Double number) {
        Locale locale = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(locale);
        return formatRupiah.format(number);
    }
}