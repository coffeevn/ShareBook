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
import com.example.along.sharebook.myInterface.SellBook;

public class SellFragment extends DialogFragment {

    public SellFragment(){
        //empty
    }
    SellBook priceBook;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        priceBook = (SellBook) getActivity();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_sell,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final EditText etPrice = view.findViewById(R.id.etSellPrice);
        TextView tvSellConfirm = view.findViewById(R.id.tvSellConfirm);
        TextView tvSellCancel = view.findViewById(R.id.tvSellCancel);

        tvSellConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try {
                   int i = Integer.parseInt(etPrice.getText().toString());
                   i=i*1000;
                   priceBook.priceOfBook(i);
                   dismiss();
               } catch (NumberFormatException e){
                   Toast.makeText(getActivity(),"Số tiền không hợp lệ",Toast.LENGTH_SHORT).show();
               }
            }
        });
        tvSellCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
