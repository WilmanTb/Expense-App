package com.wfexpense.wfexpense;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CustomTextWatcher implements TextWatcher {

    private EditText editText;
    private String current = "";

    public CustomTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String sText = s.toString();
        if (!sText.equals(current)) {
            editText.removeTextChangedListener(this);

            String cleanString = sText.replace(".", "").replace(",", "");

            try {
                double parsed = Double.parseDouble(cleanString);
                String formatted = String.format("%,.0f", parsed);
                current = formatted;
                editText.setText(formatted);
                editText.setSelection(formatted.length());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            editText.addTextChangedListener(this);
        }
    }
}
