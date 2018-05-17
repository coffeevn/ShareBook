package com.example.along.sharebook.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.along.sharebook.R;
import com.example.along.sharebook.myInterface.SignUpUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;

public class SignUpFragment extends DialogFragment {
    private boolean emailChecked = false;
    private boolean passwordChecked = false;
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    SignUpUser signUpUser;
    public SignUpFragment(){
        //Empty
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        signUpUser = (SignUpUser) getActivity();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_sign_up,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final EditText etEmail = view.findViewById(R.id.etEmail);
        final EditText etPassword = view.findViewById(R.id.etPassword);
        final EditText etRePassword = view.findViewById(R.id.etRePassword);
        TextView tvSignUp = view.findViewById(R.id.tvSignUp);
        TextView tvBack = view.findViewById(R.id.tvBack);


        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                emailChecked = false;
                if (!hasFocus){
                    if (checkEmail(etEmail.getText().toString())){
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.fetchSignInMethodsForEmail(etEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (!task.getResult().getSignInMethods().isEmpty()){
                                    etEmail.setError("Email đã được sử dụng!");
                                } else {
                                    emailChecked = true;
                                }
                            }
                        });
                    } else {
                        etEmail.setError("Email không hợp lệ!");
                    }
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passwordChecked = false;
                if (!hasFocus) {
                    if (etPassword.getText().toString().isEmpty()) {
                        etPassword.setError("Password không được trống!");
                    } else {
                        if (etPassword.getText().toString().length() < 6) {
                            etPassword.setError("Password ít hơn 6 ký tự!");
                        } else {
                            passwordChecked = true;
                        }
                    }
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailChecked && passwordChecked && etPassword.getText().toString().compareTo(etRePassword.getText().toString())==0) {
                    signUpUser.AddUser(etEmail.getText().toString(), etPassword.getText().toString());
                    dismiss();
                }else {
                    if(!emailChecked){
                        Toast.makeText(getActivity(),"Email không hợp lệ!",Toast.LENGTH_SHORT).show();
                    }
                    if(!passwordChecked){
                        Toast.makeText(getActivity(),"Mật khẩu không hợp lệ!",Toast.LENGTH_SHORT).show();
                    }
                    if(etPassword.getText().toString().compareTo(etRePassword.getText().toString())!=0){
                        Toast.makeText(getActivity(),"Mật khẩu không trùng khớp!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}
