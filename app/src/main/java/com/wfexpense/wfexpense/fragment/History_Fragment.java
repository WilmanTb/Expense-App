package com.wfexpense.wfexpense.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wfexpense.wfexpense.R;
public class History_Fragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_history_, container, false);

        return view;
    }
}