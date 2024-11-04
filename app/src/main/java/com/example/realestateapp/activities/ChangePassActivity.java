package com.example.realestateapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.realestateapp.R;
import com.example.realestateapp.databinding.ActivityChangePassBinding;
import com.example.realestateapp.databinding.ActivityProfileEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePassActivity extends AppCompatActivity {

    private static final String TAG = "CHANGE_PASSWORD_TAG";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private ActivityChangePassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private String currentPass = "";
    private String newPass = "";
    private String confirmNewPass = "";

    private void validateData() {

        Log.d(TAG, "valdateData: ");

        currentPass = binding.currentPasswordEt.getText().toString().trim();
        newPass = binding.newPasswordEt.getText().toString().trim();
        confirmNewPass = binding.confirmNewPasswordEt.getText().toString().trim();

        //validate data

        if (currentPass.isEmpty()) {
            binding.currentPasswordEt.setError("Enter current password!");
            binding.currentPasswordEt.requestFocus();

        } else if (newPass.isEmpty()) {
            binding.newPasswordEt.setError("Enter new password!");
            binding.newPasswordEt.requestFocus();

        } else if (confirmNewPass.isEmpty()) {
            binding.confirmNewPasswordEt.setError("Enter confirm password");
            binding.confirmNewPasswordEt.requestFocus();

        } else if (!newPass.equals(confirmNewPass)) {
            binding.confirmNewPasswordEt.setError("Password doesn't match");
            binding.confirmNewPasswordEt.requestFocus();
        } else {
            authenticateUserForUpdatePassword();
        }
    }

    private void authenticateUserForUpdatePassword() {
        Log.d(TAG, "authenticateUserForUpdatePassword: ");
        progressDialog.setMessage("Authen User");
        progressDialog.show();

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPass);
     
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Authen Success!");
                        updatePassword();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                        progressDialog.dismiss();
                        MyUtils.toast(ChangePassActivity.this,"Failure"+e.getMessage());
                    }
                });
    }

    private  void updatePassword(){
        Log.d(TAG, "updatePassword: ");

        progressDialog.setMessage("Updating password");
        progressDialog.show();
        firebaseUser.updatePassword(newPass)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Pass Update" );
                        progressDialog.dismiss();
                        MyUtils.toast(ChangePassActivity.this,"Succsess");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                        progressDialog.dismiss();
                        MyUtils.toast(ChangePassActivity.this,"Failure"+e.getMessage());

                    }
                });
    }
}