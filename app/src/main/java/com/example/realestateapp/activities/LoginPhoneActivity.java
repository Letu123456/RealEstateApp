package com.example.realestateapp.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.realestateapp.databinding.ActivityLoginPhoneBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginPhoneActivity extends AppCompatActivity {

    private ActivityLoginPhoneBinding binding;
    private static final String TAG = "LOGIN_PHONE_TAG";
    //ProgressDialog to show while phone login, saving user info
    private ProgressDialog progressDialog;
    //Firebase Auth for auth related tasks
    private FirebaseAuth firebaseAuth;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_phone_login.xml = Activity LoginPhoneBinding
        binding = ActivityLoginPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //For the start show phone input UI and hide OTP UI
        binding.phoneInputRl.setVisibility(View.VISIBLE);
        binding.otpInputRl.setVisibility(View.GONE);
        //init/setup ProgressDialog to show while login
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //Firebase Auth for auth related tasks
        firebaseAuth = FirebaseAuth.getInstance();
        phoneLoginCallBack();
        //handle toolbarBackBtn click, go-back
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.send0tpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();

            }
        });
        binding.resend0tpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(forceResendingToken);
            }
        });
        binding.verify0tpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = binding.otpEt.getText().toString().trim();
                if (otp.isEmpty()){
                    binding.otpEt.setError("Enter OTP");
                    binding.otpEt.requestFocus();
                } else if (otp.length() < 6){
                    binding.otpEt.setError("OTP length must be 6 characters");
                    binding.otpEt.requestFocus();
                } else {
                    verifyPhoneNumberWithCode(otp);
                }
            }
        });
    }

    private String phoneCode = "", phoneNumber = "", phoneNumberWithCode = "";

    private void validateData() {
        phoneCode = binding.phoneCodeTil.getSelectedCountryCodeWithPlus();
        phoneNumber = binding.phoneNumberEt.getText().toString().trim();
        phoneNumberWithCode = phoneCode + phoneNumber;
        Log.d(TAG, "validateData: Phone Code: " + phoneCode);
        Log.d(TAG, "validateData: Phone Number: " + phoneNumber);
        Log.d(TAG, "validateData: Phone Number With Code: " + phoneNumberWithCode);
        if (phoneNumber.isEmpty()) {
            binding.phoneNumberEt.setError("Enter Phone Number");
            binding.phoneNumberEt.requestFocus();
        } else {
            startPhoneNumberVerification();
        }
    }

    private void startPhoneNumberVerification() {
        progressDialog.setMessage("Sending OTP to " + phoneNumberWithCode);
        progressDialog.show();
        //Setup Phone Auth Options with phone number, time out, callback etc.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumberWithCode)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBacks)
                        .build();
//Start phone verification with PhoneAuthOptions
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        progressDialog.setMessage("Resending OTP to " + phoneNumberWithCode);
        progressDialog.show();
//Setup Phone Auth Options with phone number, time out, callback etc.
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumberWithCode)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks)
                .setForceResendingToken(token)
                .build();
//Start phone verification with PhoneAuthOptions PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyPhoneNumberWithCode(String otp){
        Log.d(TAG,"verifyPhone NumberWithCode: OTP: "+otp);
//show progress
        progressDialog.setMessage("Verifying OTP...");
        progressDialog.show();
//PhoneAuthCredential with verification id and OTP to signIn user with signInWithPhoneAuthCredential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);

    }

    private void phoneLoginCallBack() {
        Log.d(TAG, "phoneLoginCallBack: ");
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Log.d(TAG, "onCodeSent");
                mVerificationId = verificationId;
                forceResendingToken = token;
                progressDialog.dismiss();
                binding.phoneInputRl.setVisibility(View.GONE);
                binding.otpInputRl.setVisibility(View.VISIBLE);
                MyUtils.toast(LoginPhoneActivity.this, "OTP sent to" + phoneNumberWithCode);
                binding.loginPhoneLabel.setText("Please type verification code sent to " + phoneNumberWithCode);

            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: ");
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed: ", e);

                MyUtils.toast(LoginPhoneActivity.this, "Failed to verify due to " + e.getMessage());
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: ");
        //show progress
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        //Signin in to firebase auth using Phone Credentials
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: ");
                        //SignIn Success, let's check if the user is new (New Account Register) or existing (Existing Login)
                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            //New User, Account created. Let's save user info to firebase realtime database
                            Log.d(TAG, "onSuccess: New User, Account created...");
                            updateUserInfo();
                        } else {
                            Log.d(TAG, " onSuccess: Existing User, logged In...");
                            //New User, Account created. No need to save user info to firebase realtime database, Start MainActivity
                            startActivity(new Intent(LoginPhoneActivity.this, MainActivity.class));
                            finishAffinity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //SignIn failed, show exception message
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        MyUtils.toast(LoginPhoneActivity.this, "Login Failed due to " + e.getMessage());
                    }
                });
    }

    private void updateUserInfo() {
        Log.d(TAG, "updateUserInfo: ");
//show progress
        progressDialog.setMessage("Saving User Info...");
        progressDialog.show();
        //Let's save user info to Firebase Realtime database key names should be same as we done in Register User via email and Google
        //get current timestamp e.g. to show user registration date/time
        long timestamp = MyUtils.timestamp();
        String registeredUserUid = firebaseAuth.getUid();
        //setup data to save in firebase realtime db. most of the data will be empty and will set in edit profile
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", registeredUserUid);
        hashMap.put("email", "");
        hashMap.put("name", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("phoneCode", "" + phoneCode);
        hashMap.put("phoneNumber", "" + phoneNumber);
        hashMap.put("profileImageUrl", "");
        hashMap.put("dob", "");
        hashMap.put("userType", "" + MyUtils.USER_TYPE_PHONE);//possible values Email/Phone/Google
        hashMap.put("token", ""); //FCM token to send push notifications
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(registeredUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: User info saved...");
                        progressDialog.dismiss();

                        startActivity(new Intent(LoginPhoneActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        MyUtils.toast(LoginPhoneActivity.this, "Failed to save due to " + e.getMessage());
                    }
                });
    }
}