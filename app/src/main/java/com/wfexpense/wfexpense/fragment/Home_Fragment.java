package com.wfexpense.wfexpense.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wfexpense.wfexpense.Global_Data;
import com.wfexpense.wfexpense.R;
import com.wfexpense.wfexpense.adapter.Adapter_List_Expense;
import com.wfexpense.wfexpense.adapter.Adapter_List_Income;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Home_Fragment extends Fragment {

    private View view;
    private ImageView btn_select_month;
    public static String namaBulan;
    private TextView month_name, txt_income_bottom, txt_expense_bottom, txt_total_balance, txt_income, txt_expense;
    private RecyclerView rc_expense, rc_income;
    private Adapter_List_Expense adapter_list_expense;
    private Adapter_List_Income adapter_list_income;
    private ArrayList<CharSequence> expenseList;
    private ArrayList<CharSequence> incomeList;
    private DatabaseReference dbBalance;
    private CardView cv_incomebottom, cv_expensebottom;
    private String totalBalance;
    private int totalExpense = 0, totalIncome = 0;
    private boolean expenseMonthExist;
    private boolean incomeMonthExist;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ConstraintLayout main_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_, container, false);

        initComponents();
        getCurrentDate();
        shimmerFrameLayout.startShimmerAnimation();
        getBalanceData();

        btn_select_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthList(v);
            }
        });

        return view;
    }

    private void initComponents() {
        btn_select_month = view.findViewById(R.id.btn_select_month);
        txt_expense_bottom = view.findViewById(R.id.txt_expense_bottom);
        txt_income_bottom = view.findViewById(R.id.txt_income_bottom);
        cv_expensebottom = view.findViewById(R.id.cv_expensebottom);
        cv_incomebottom = view.findViewById(R.id.cv_incomebottom);
        txt_total_balance = view.findViewById(R.id.txt_total_balance);
        txt_income = view.findViewById(R.id.txt_income);
        txt_expense = view.findViewById(R.id.txt_expense);
        month_name = view.findViewById(R.id.month_name);
        rc_expense = view.findViewById(R.id.rc_expense);
        rc_expense.setHasFixedSize(true);
        rc_expense.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseList = new ArrayList<>();
        adapter_list_expense = new Adapter_List_Expense(getContext(), expenseList);
        rc_expense.setAdapter(adapter_list_expense);

        dbBalance = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();

        rc_income = view.findViewById(R.id.rc_income);
        rc_income.setHasFixedSize(true);
        rc_income.setLayoutManager(new LinearLayoutManager(getContext()));
        incomeList = new ArrayList<>();
        adapter_list_income = new Adapter_List_Income(getContext(), incomeList);
        rc_income.setAdapter(adapter_list_income);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_home);
        main_view = view.findViewById(R.id.main_view);
    }

    private void showMonthList(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_month, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedMonth = item.getTitle().toString();
                month_name.setText(selectedMonth);
                getBalanceData();
                return true;
            }
        });
        popupMenu.show();
    }

    private void showExpenseComponents() {
        if (!expenseList.isEmpty()) {
            rc_expense.setVisibility(View.VISIBLE);
            txt_expense_bottom.setVisibility(View.VISIBLE);
            cv_expensebottom.setVisibility(View.VISIBLE);
            txt_expense_bottom.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_animation));
            cv_expensebottom.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_animation));
        } else {
            rc_expense.setVisibility(View.GONE);
            txt_expense_bottom.setVisibility(View.GONE);
            cv_expensebottom.setVisibility(View.GONE);
        }
    }

    private void showIncomeComponents() {
        if (!incomeList.isEmpty()) {
            rc_income.setVisibility(View.VISIBLE);
            txt_income_bottom.setVisibility(View.VISIBLE);
            cv_incomebottom.setVisibility(View.VISIBLE);
            txt_income_bottom.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_animation));
            cv_incomebottom.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_animation));
        } else {
            rc_income.setVisibility(View.GONE);
            txt_income_bottom.setVisibility(View.GONE);
            cv_incomebottom.setVisibility(View.GONE);
        }
    }

    private void getBalanceData() {
        dbBalance.child("Balance").child("Expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    totalExpense = 0;
                    expenseList.clear();
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot expenseSnapshot : dateSnapshot.getChildren()) {
                            if (expenseSnapshot.child("bulan").getValue().toString().equals(month_name.getText().toString())) {
                                expenseMonthExist = true;
                                String harga = expenseSnapshot.child("harga").getValue(String.class);
                                if (harga != null) {
                                    totalExpense += Integer.parseInt(harga);
                                }
                            } else expenseMonthExist = false;
                        }
                        if (expenseMonthExist) {
                            String dataKey = dateSnapshot.getKey();
                            expenseList.add(dataKey);
                        }
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(()->{
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        main_view.setVisibility(View.VISIBLE);
                            },2000);
                    adapter_list_expense.notifyDataSetChanged();
                    showExpenseComponents();

                    dbBalance.child("Balance").child("Income").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                totalIncome = 0;
                                incomeList.clear();
                                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                    for (DataSnapshot incomeSnapshot : dateSnapshot.getChildren()) {
                                        if (incomeSnapshot.child("bulan").getValue().toString().equals(month_name.getText().toString())) {
                                            incomeMonthExist = true;
                                            String harga = incomeSnapshot.child("harga").getValue(String.class);
                                            if (harga != null) {
                                                totalIncome += Integer.parseInt(harga);
                                            }
                                        } else incomeMonthExist = false;
                                    }
                                    if (incomeMonthExist) {
                                        String dataKey = dateSnapshot.getKey();
                                        incomeList.add(dataKey);
                                    }
                                }

                                adapter_list_income.notifyDataSetChanged();
                                showIncomeComponents();

                                txt_income.setText(formatRupiah(Double.parseDouble(String.valueOf(totalIncome))));
                                dbBalance.child("Tabungan").child(month_name.getText().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int hasilBalance = 0;
                                        if (snapshot.exists()) {
                                            hasilBalance = totalIncome - totalExpense;
                                            txt_total_balance.setText(formatRupiah(Double.parseDouble(String.valueOf(hasilBalance))));
                                            dbBalance.child("Tabungan").child(month_name.getText().toString()).child("total").setValue(String.valueOf(hasilBalance));
                                        } else {
                                            txt_total_balance.setText(formatRupiah(Double.parseDouble(String.valueOf(hasilBalance))));
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
                    txt_expense.setText(formatRupiah(Double.parseDouble(String.valueOf(totalExpense))));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String formatRupiah(Double number) {
        Locale locale = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(locale);
        return formatRupiah.format(number);
    }

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.US);
        String currentMonthName = simpleDateFormat.format(calendar.getTime());
        if (month_name.getText().toString().equals("month")) {
            month_name.setText(currentMonthName);
            namaBulan = month_name.getText().toString();
        }
    }

}