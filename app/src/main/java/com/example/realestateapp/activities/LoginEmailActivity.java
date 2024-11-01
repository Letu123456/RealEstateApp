package com.example.realestateapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.realestateapp.R;
import com.example.realestateapp.databinding.ActivityLoginEmailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginEmailActivity extends AppCompatActivity {

    private ActivityLoginEmailBinding binding;

    private static final String TAG ="LOGIN_TAG";

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.loginBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
validateData();
            }
        });

        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginEmailActivity.this, RegisterEmailActivity.class));
            }
        });

        }

        private String email,password;

    private void validateData(){
        email = binding.emailEt.getText().toString().trim();
        password=binding.passwordEt.getText().toString();

        Log.d(TAG,"validateData: Email:"+email);
        Log.d(TAG,"validateData: Password:"+password);
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Invalid Email!");
            binding.emailEt.requestFocus();
        } else if (password.isEmpty()) {

            binding.passwordEt.setError("Enter Password!");
            binding.passwordEt.requestFocus();

        }else{

        }
    }

    private void loginUser(){
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Log.d(TAG,"onSuccess: Logged In..");
                        progressDialog.show();

                        startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure",e);
                        progressDialog.dismiss();
                        MyUtils.toast(LoginEmailActivity.this,"Failure due to "+e.getMessage());
                    }
                });

    }
    }
