package com.example.along.sharebook.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.along.sharebook.R;
import com.example.along.sharebook.myInterface.RentBook;

public class RentFragment extends DialogFragment {

    public RentFragment(){
        //empty
    }
    RentBook rentBook;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rentBook = (RentBook) getActivity();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_rent,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final EditText etPrice = view.findViewById(R.id.etRentPrice);
        TextView tvRentConfirm = view.findViewById(R.id.tvRentConfirm);
        TextView tvRentCancel = view.findViewById(R.id.tvRentCancel);

        tvRentConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int i = Integer.parseInt(etPrice.getText().toString());
                    i=i*1000;
                    rentBook.priceOfRent(i);
                    dismiss();
                } catch (NumberFormatException e){
                    Toast.makeText(getActivity(),"Số tiền không hợp lệ",Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvRentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
