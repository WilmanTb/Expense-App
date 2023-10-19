package com.wfexpense.wfexpense.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wfexpense.wfexpense.CustomTextWatcher;
import com.wfexpense.wfexpense.Global_Data;
import com.wfexpense.wfexpense.R;
import com.wfexpense.wfexpense.databinding.ActivityMainBinding;
import com.wfexpense.wfexpense.fragment.History_Fragment;
import com.wfexpense.wfexpense.fragment.Home_Fragment;
import com.wfexpense.wfexpense.model.Balance_Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityMainBinding activityMainBinding;
    private FloatingActionButton botton_add;
    private DatabaseReference dbBalance;
    private String jenisTambah = "", jenisExpense = "", namaPengeluaran, jumlahPengeluaran, tanggalPengeluaran, jenisPengeluaran;
    private Spinner spin_jenisExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        setBottomNavigationCornerRadius();

        replaceFragment(new Home_Fragment());

        initComponents();

        bindingFragment();


        botton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });


    }

    private void initComponents() {
        botton_add = findViewById(R.id.botton_add);
        dbBalance = FirebaseDatabase.getInstance(Global_Data.dbUrl).getReference();

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_fragment_animation, R.anim.exit_right_to_left  ,R.anim.left_fragment_animation,R.anim.exit_left_to_right);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    private void setBottomNavigationCornerRadius() {
        MaterialShapeDrawable bottomBarBackground = (MaterialShapeDrawable) activityMainBinding.bottomAppbar.getBackground();
        bottomBarBackground.setShapeAppearanceModel(
                bottomBarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(CornerFamily.ROUNDED, 50)
                        .setTopLeftCorner(CornerFamily.ROUNDED, 50)
                        .build());
    }

    private void bindingFragment() {
        activityMainBinding.bottomNav.setBackground(null);
        activityMainBinding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        replaceFragment(new Home_Fragment());
                        break;

                    case R.id.history:
                        replaceFragment(new History_Fragment());
                        break;
                }
                return true;
            }
        });
    }

    private void showBottomSheet() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        LinearLayout addIncome = dialog.findViewById(R.id.add_income);
        LinearLayout addExpense = dialog.findViewById(R.id.add_expense);
        ImageView btnClose = dialog.findViewById(R.id.btn_close);

        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem("Income");
                dialog.dismiss();
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem("Expense");
                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void addItem(String jenisTambah) {
        Calendar calendar = Calendar.getInstance();
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_income_expense);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText et_expense_name = dialog.findViewById(R.id.et_nama_pengeluaran);
        EditText et_expense_total = dialog.findViewById(R.id.et_jumlah_pengeluaran);
        EditText et_expense_date = dialog.findViewById(R.id.et_tanggal_pengeluaran);
        Button btn_simpan = dialog.findViewById(R.id.btn_simpan);
        AppCompatButton btn_batal = dialog.findViewById(R.id.btn_batal);
        ImageView btn_tanggal = dialog.findViewById(R.id.btn_tanggal);
        spin_jenisExpense = dialog.findViewById(R.id.spin_jenisExpense);
        TextView txt_namaPengeluaran = dialog.findViewById(R.id.txt_namaPengeluaran);
        TextView txt_jumlahPengeluaran = dialog.findViewById(R.id.txt_jumlahPengeluaran);
        TextView txt_jenisPengeluaran = dialog.findViewById(R.id.txt_jenisPengeluaran);
        TextView txt_tanggal_pengeluaran = dialog.findViewById(R.id.txt_tanggal_pengeluaran);
        View spin_line = dialog.findViewById(R.id.spin_line);

        et_expense_name.setText("");
        et_expense_total.setText("");
        et_expense_date.setText("");
        et_expense_total.addTextChangedListener(new CustomTextWatcher(et_expense_total));

        ArrayAdapter<CharSequence> adapterJenisExpense;


        if (jenisTambah.equals("Expense")){
            namaPengeluaran = "Nama expense";
            jenisPengeluaran = "Jenis expense";
            tanggalPengeluaran = "Tanggal expense";
            jumlahPengeluaran = "Jumlah expense";
            adapterJenisExpense = ArrayAdapter.createFromResource(this, R.array.ExpenseList, android.R.layout.simple_spinner_item);
            adapterJenisExpense.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_jenisExpense.setAdapter(adapterJenisExpense);
            spin_jenisExpense.setOnItemSelectedListener(this);
        } else {
            namaPengeluaran = "Nama income";
            tanggalPengeluaran = "Tanggal income";
            jumlahPengeluaran = "Jumlah income";
            spin_jenisExpense.setVisibility(View.GONE);
            txt_jenisPengeluaran.setVisibility(View.GONE);
            jenisExpense = "";
            spin_line.setVisibility(View.GONE);
        }
        txt_namaPengeluaran.setText(namaPengeluaran);
        txt_jumlahPengeluaran.setText(jumlahPengeluaran);
        txt_jenisPengeluaran.setText(jenisPengeluaran);
        txt_tanggal_pengeluaran.setText(tanggalPengeluaran);

        btn_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                calendar.set(year, month, day);

                                // Format the selected date as "dd-MM-yyyy"
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                String formattedDate = dateFormat.format(calendar.getTime());

                                // Display the formatted date in the EditText
                                et_expense_date.setText(formattedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expenseName = et_expense_name.getText().toString();
                String expenseTotal = et_expense_total.getText().toString();
                String expesenseDate = et_expense_date.getText().toString();
                String cleanTotal = expenseTotal.replace(",", "");

//                if (expenseName.isEmpty() || expenseTotal.isEmpty() || expesenseDate.isEmpty())
//                    Toast.makeText(MainActivity.this, "Form ga boleh kosong yaaa", Toast.LENGTH_SHORT).show();
//                else {
                    String idInput = dbBalance.push().getKey();
                    Balance_Model balance_model = new Balance_Model(idInput, expenseName, jenisTambah.toLowerCase(), jenisExpense, expesenseDate, cleanTotal, Home_Fragment.namaBulan);
                    dbBalance.child("Balance").child(jenisTambah).child(expesenseDate).child(idInput).setValue(balance_model);
                    Toast.makeText(MainActivity.this, "Yay! Data berhasil diinput ;)", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
//                }
            }
        });

        btn_batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spin_jenisExpense = (Spinner) parent;
        jenisExpense = spin_jenisExpense.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}