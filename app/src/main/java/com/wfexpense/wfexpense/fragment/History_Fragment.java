package com.wfexpense.wfexpense.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wfexpense.wfexpense.Global_Data;
import com.wfexpense.wfexpense.R;
import com.wfexpense.wfexpense.adapter.Adapter_History;
import com.wfexpense.wfexpense.model.Balance_Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class History_Fragment extends Fragment {

    View view;
    RecyclerView rc_historyToday;
    Adapter_History adapter_history;
    ArrayList<CharSequence> dateBalanceList;
    ArrayList<Balance_Model> balanceList;
    DatabaseReference dbBalance;
    String currentDate;
    ShimmerFrameLayout shimmerFrameLayout;
    TextView txt_hari_ini, txt_info;
    ImageView img_list_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_, container, false);

        initComponents();
        shimmerFrameLayout.startShimmerAnimation();
        getCurrentDate();
        getBalanceActivityData();
        callShimmerLayout();

        return view;
    }

    private void initComponents() {
        dbBalance = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();

        rc_historyToday = view.findViewById(R.id.rc_historyToday);
        rc_historyToday.setHasFixedSize(true);
        rc_historyToday.setLayoutManager(new LinearLayoutManager(getContext()));
        dateBalanceList = new ArrayList<>();
        balanceList = new ArrayList<>();
        adapter_history = new Adapter_History(getContext(), dateBalanceList);
        rc_historyToday.setAdapter(adapter_history);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_history);
        txt_hari_ini = view.findViewById(R.id.txt_hari_ini);
        txt_info = view.findViewById(R.id.txt_info);
        img_list_empty = view.findViewById(R.id.img_list_empty);

    }

    private void getBalanceActivityData() {
//        ValueEventListener expenseListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    dateBalanceList.clear();
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        String dateBalance = currentDate;
//                        dateBalanceList.add(dateBalance);
//                    }
//                    adapter_history.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        ValueEventListener incomeListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    dateBalanceList.clear();
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        String dateBalance = currentDate;
//                        dateBalanceList.add(dateBalance);
//                    }
//                    adapter_history.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        dbBalance.child("Balance").child("Expense").child(currentDate).addValueEventListener(expenseListener);
//        dbBalance.child("Balance").child("Expense").child(currentDate).addValueEventListener(expenseListener);

        dbBalance.child("Balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key =  dataSnapshot.getKey();
                    dbBalance.child("Balance").child(key).child(currentDate).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String historyDate = currentDate;
                                dateBalanceList.add(historyDate);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                adapter_history.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void callShimmerLayout(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
            if (dateBalanceList.isEmpty()) {
                txt_info.setVisibility(View.VISIBLE);
                img_list_empty.setVisibility(View.VISIBLE);
            } else {
                rc_historyToday.setVisibility(View.VISIBLE);
                txt_hari_ini.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = simpleDateFormat.format(calendar.getTime());
    }
}