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

public class Adapter_List_Data_Income extends RecyclerView.Adapter<Adapter_List_Data_Income.ViewHolder> {

    Context context;
    ArrayList<Balance_Model> myList;
    DatabaseReference dbIncome = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();

    public Adapter_List_Data_Income(Context context, ArrayList<Balance_Model> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public Adapter_List_Data_Income.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_data_income, parent, false);
        return new Adapter_List_Data_Income.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_List_Data_Income.ViewHolder holder, int position) {
        Balance_Model balance_model = myList.get(position);
        holder.income_item_name.setText(balance_model.getNama());
        holder.income_item_cost.setText(formatRupiah(Double.parseDouble(balance_model.getHarga())));

        holder.edit_income_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_item_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText et_expense_name = dialog.findViewById(R.id.et_nama_pengeluaran);
                EditText et_expense_total = dialog.findViewById(R.id.et_jumlah_pengeluaran);
                TextView txt_jumlahPengeluaran = dialog.findViewById(R.id.txt_jumlahPengeluaran);
                TextView txt_namaPengeluaran = dialog.findViewById(R.id.txt_namaPengeluaran);
                Button btn_simpan = dialog.findViewById(R.id.btn_simpan);
                AppCompatButton btn_batal = dialog.findViewById(R.id.btn_batal);

                String namaIncome = "Nama income";
                String jumlahIncome = "Jumlah income";
                txt_jumlahPengeluaran.setText(jumlahIncome);
                txt_namaPengeluaran.setText(namaIncome);
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
                            dbIncome.child("Balance").child("Income").child(balance_model.getTanggal()).child(balance_model.getId()).child("nama").setValue(expenseName);
                            dbIncome.child("Balance").child("Income").child(balance_model.getTanggal()).child(balance_model.getId()).child("harga").setValue(cleanText);
                            Toast.makeText(context, "Yay! Data nya berhasil diubah ;)", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        holder.remove_income_item.setOnClickListener(new View.OnClickListener() {
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
                        dbIncome.child("Balance").child("Income").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String key = dataSnapshot.getKey();
                                    dbIncome.child("Balance").child("Income").child(key).child(myList.get(holder.getAdapterPosition()).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                dbIncome.child("Balance").child("Income").child(key).child(myList.get(holder.getAdapterPosition()).getId()).removeValue();
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
        TextView income_item_name, income_item_cost;
        ImageView edit_income_item, remove_income_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            income_item_cost = itemView.findViewById(R.id.income_item_cost);
            income_item_name = itemView.findViewById(R.id.income_item_name);
            edit_income_item = itemView.findViewById(R.id.edit_income_item);
            remove_income_item = itemView.findViewById(R.id.remove_income_item);
        }
    }

    private String formatRupiah(Double number){
        Locale locale = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(locale);
        return formatRupiah.format(number);
    }
}