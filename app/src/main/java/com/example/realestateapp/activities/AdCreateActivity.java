package com.example.realestateapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.example.realestateapp.Manifest;
import android.Manifest;
import com.example.realestateapp.R;
import com.example.realestateapp.databinding.ActivityAdCreateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdCreateActivity extends AppCompatActivity {

    private ActivityAdCreateBinding binding;
    private static  final String TAG ="AD_CREATE_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri=null;
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagesPicked adapterImagesPicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this,R.layout.row_category,MyUtils.categories);
        binding.categoryAct.setAdapter(adapterCategories);

        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this,R.layout.row_condition,MyUtils.conditions);
        binding.conditionAct.setAdapter(adapterConditions);


        imagePickedArrayList =new ArrayList<>();
        loadImanges();
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.toolbarAdImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickOption();
            }
        });

        binding.poastAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            validateData();
            }
        });


    }
private void loadImanges(){
        Log.d(TAG,"loadImages:");
        adapterImagesPicked = new AdapterImagesPicked(this,imagePickedArrayList);
        binding.imagesRv.setAdapter(adapterImagesPicked);
}


    private void showImagePickOption(){
        Log.d(TAG,"showImagePickOptions:");

        PopupMenu popupMenu = new PopupMenu(this,binding.toolbarAdImageBtn);
        popupMenu.getMenu().add(Menu.NONE,1,1,"Camera");
        popupMenu.getMenu().add(Menu.NONE,2,2,"Gallery");

        popupMenu.show();


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if(itemId ==1){

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                        String[] cameraPermession = new String[]{Manifest.permission.CAMERA};
                        requestCameraPermission.launch(cameraPermession);
                    }else{
                        String[] cameraPermession = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestCameraPermission.launch(cameraPermession);

                    }
                }else if(itemId == 2){

                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
                        pickImageGallery();
                    }else{
                        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        requestStoragePermission.launch(storagePermission);
                    }


                }

                return true;
            }
        });


    }

    private ActivityResultLauncher<String > requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean o) {
                    Log.d(TAG,"onACtivityResult: isGrandted"+o);
                    if(o){

                        pickImageGallery();
                    }else{
                        MyUtils.toast(AdCreateActivity.this,"Storage Permission denied...");

                    }
                }
            }


    );
    private ActivityResultLauncher<String[]> requestCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> o) {
                    Log.d(TAG,"onActivityResult:");
                    Log.d(TAG,"onActivityResult:"+o.toString());

                    boolean areAllGranted = true;
                    for(Boolean isGranted: o.values()){
                        areAllGranted=areAllGranted&&isGranted;

                    }

                    if(areAllGranted){

                        pickImageCamera();
                    }else{
                        MyUtils.toast(AdCreateActivity.this,"Camera or Storage or both permissions denied..");

                    }

                }
            }
    );


    private void pickImageGallery(){
        Log.d(TAG,"pickImageGallery:");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLaunch.launch(intent);

    }

    private void pickImageCamera(){
        Log.d(TAG,"pickImageCamera:");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"TEMPORARY_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"TEMPORARY_IMAGE_DECRIPTION");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLaunch.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLaunch=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult:imageUri" + imageUri);
                        String timestamp = "" + System.currentTimeMillis();
                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);

                        imagePickedArrayList.add(modelImagePicked);
                        loadImanges();
                    } else {
                        MyUtils.toast(AdCreateActivity.this, "Cancelled");
                    }
                }
            }

    );
    private final ActivityResultLauncher<Intent> cameraActivityResultLaunch=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {

                        Log.d(TAG, "onActivityResult:imageUri" + imageUri);
                        String timestamp = "" + System.currentTimeMillis();
                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);

                        imagePickedArrayList.add(modelImagePicked);
                        loadImanges();
                    } else {
                        MyUtils.toast(AdCreateActivity.this, "Cancelled");
                    }
                }
            }

    );

    private String brand ="",category="",condition="",address="",price="",title="",description="";

    private double latitude=0;
    private double longitude =0;

    private void validateData(){
        Log.d(TAG,"validateData:");
        brand = binding.brandEt.getText().toString().trim();
        category=binding.categoryAct.getText().toString().trim();
        condition=binding.conditionAct.getText().toString().trim();
        address=binding.locationAct.getText().toString().trim();
        price=binding.priceEt.getText().toString().trim();
        title=binding.titleEt.getText().toString().trim();
        description=binding.decriptionEt.getText().toString().trim();

        if(brand.isEmpty()){
            binding.brandEt.setError("Enter Brand");
            binding.brandEt.requestFocus();
        } else if (category.isEmpty()) {
            binding.categoryAct.setError("Choose Category");
            binding.categoryAct.requestFocus();
        } else if (condition.isEmpty()) {
            binding.conditionAct.setError("Choose Condition");
            binding.conditionAct.requestFocus();
        } else  if (title.isEmpty()) {
            binding.titleEt.setError("Enter Title");
            binding.titleEt.requestFocus();
        } else if (description.isEmpty()) {
            binding.decriptionEt.setError("Enter Description");
            binding.decriptionEt.requestFocus();
        } else if (imagePickedArrayList.isEmpty()) {
            MyUtils.toast(this,"Picl at least one image");
        }else {
            postAd();
        }
    }

    private void postAd(){
        Log.d(TAG,"postAd:");

        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();
        long timestamp= MyUtils.timestamp();
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = refAds.push().getKey();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("id",""+keyId);
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("brand",""+brand);
        hashMap.put("category",""+category);
        hashMap.put("condition",""+condition);
        hashMap.put("address",""+address);
        hashMap.put("price",""+price);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("status",MyUtils.STATUS_AVAILABLE);
        hashMap.put("timestamp",timestamp);
        hashMap.put("latitude",latitude);
        hashMap.put("longitude",longitude);

        refAds.child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSucccess:Ad Published");

                        uploadImagesStorage(keyId);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure:",e);
                        progressDialog.dismiss();;
                        MyUtils.toast(AdCreateActivity.this,"Failure to publish Ad due to:"+e.getMessage());
                    }
                });


    }

    private void uploadImagesStorage(String adId){
        Log.d(TAG,"uploadImagesStorage:");
        for(int i=0;i<imagePickedArrayList.size();i++){
            ModelImagePicked modelImagePicked=imagePickedArrayList.get(i);
            String imageName = modelImagePicked.getId();
            String filePathAndName ="Ads/"+imageName;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            int imageInForProgress = i+1;
            storageReference.putFile(modelImagePicked.getImageUri())
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            String message ="Uploading"+imageInForProgress+"of"+imagePickedArrayList.size()+"image...\nprogress"+(int)progress+"%";
                            Log.d(TAG,"onProgress:mesage:"+message);
                            progressDialog.setMessage(message);
                            progressDialog.show();

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Log.d(TAG,"onSuccess:");
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri uploadedImageUrl =uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("id",""+modelImagePicked.getId());
                                hashMap.put("imageUrl",""+uploadedImageUrl);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                                ref.child(adId).child("Images")
                                        .child(imageName)
                                        .updateChildren(hashMap);
                            }
                            progressDialog.dismiss();
                            }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG,"onFailure",e);
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}