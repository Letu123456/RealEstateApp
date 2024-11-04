package com.example.realestateapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestateapp.R;
import com.example.realestateapp.databinding.ActivityLoginOptionBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginOptionActivity extends AppCompatActivity {
private ActivityLoginOptionBinding binding;
private static final String TAG="LOGIN_OPTIONS_TAG";
private ProgressDialog progressDialog;
private FirebaseAuth firebaseAuth;
private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityLoginOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        binding.shipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

beginGoogleLogin();
            }
        });

        binding.loginEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginOptionActivity.this,LoginEmailActivity.class));
            }
        });

        binding.loginPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginOptionActivity.this, LoginPhoneActivity.class));
            }
        });

    }

        private void beginGoogleLogin() {
            Log.d(TAG, "beginGoogle:");
            Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInnARL.launch(googleSignInIntent);
        }

        private ActivityResultLauncher<Intent> googleSignInnARL = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Log.d(TAG,"onActivityResult:");
                        if(o.getResultCode()== Activity.RESULT_OK){
                            Intent data = o.getData();

                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try{
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                Log.d(TAG,"onActivityResult: AccountID:"+account.getId());
                                firebaseAuthWithGoogle(account.getIdToken());

                            } catch (ApiException e) {
                                Log.e(TAG,"onActivityResult:",e);

                            }
                        }else{
                            Log.d(TAG,"onActivity:Cancelled..!");
                            MyUtils.toast(LoginOptionActivity.this,"Cancelled...!");


                        }
                    }
                }
        );


    private void firebaseAuthWithGoogle(String idToken){
        Log.d(TAG,"firebaseAuthWithGoogle: idToken"+idToken);


        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(authResult.getAdditionalUserInfo().isNewUser()){
Log.d(TAG,"onSuccess: Account Created...!");
updateUserInfoDb();
                }else{
Log.d(TAG,"onSuccess:Logged In..!");
startActivity(new Intent(LoginOptionActivity.this, MainActivity.class));
finishAffinity();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure:",e);
                    }
                });
    }

    private void updateUserInfoDb(){
        Log.d(TAG,"updateUserInfoDb");
        progressDialog.setMessage("Saving user infor...!");
        progressDialog.show();

        long timestamp= MyUtils.timestamp();
         String registeredUserUid = firebaseAuth.getUid();
         String registeredUserEmail = firebaseAuth.getCurrentUser().getEmail();
         String name = firebaseAuth.getCurrentUser().getDisplayName();


         HashMap<String, Object> hashMap = new HashMap<>();

         hashMap.put("uid", registeredUserUid);
         hashMap.put("email",registeredUserEmail);
         hashMap.put("name",name);
         hashMap.put("timestamp",timestamp);
         hashMap.put("phoneCode","");
         hashMap.put("phoneNumber","");
         hashMap.put("profileImageUrl","");
         hashMap.put("dob","");
         hashMap.put("userType",MyUtils.USER_TYPE_GOOGLE);
         hashMap.put("token","");


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(registeredUserUid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess:User info saved...!");
                        progressDialog.dismiss();

                        startActivity(new Intent(LoginOptionActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure:",e);
                        progressDialog.dismiss();
                        MyUtils.toast(LoginOptionActivity.this,"Failed to save due to "+e.getMessage());
                    }
                });
    }
    }


