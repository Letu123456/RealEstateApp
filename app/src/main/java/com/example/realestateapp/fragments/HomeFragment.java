package com.example.realestateapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.realestateapp.R;
import com.example.realestateapp.activities.AdapterAd;
import com.example.realestateapp.activities.AdapterCategory;
import com.example.realestateapp.activities.ModelAd;
import com.example.realestateapp.activities.ModelCategory;
import com.example.realestateapp.activities.MyUtils;
import com.example.realestateapp.activities.RvListenerCategory;
import com.example.realestateapp.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String TAG="HOME_TAG";

    private Context hContext;

    private ArrayList<ModelAd> adArrayList;
    private AdapterAd adapterAd;

    private SharedPreferences locationSp;

    private double currentLatitude=0.0;
    private double currentlongitude=0.0;
    private String currentAddress="";


    @Override
    public void onAttach(@NonNull Context context) {
        hContext=context;
        super.onAttach(context);
    }

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(LayoutInflater.from(hContext),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*locationSp=hContext.getSharedPreferences("LOCATION_SP",Context.MODE_PRIVATE);
        currentLatitude=locationSp.getFloat("CURRENT_LATITUDE",0.0f);
        currentlongitude=locationSp.getFloat("CURRENT_LONGITUDE",0.0f);
        currentAddress=locationSp.getString("CURRENT_ADDRESS","");

        if(currentLatitude!=0.0 && currentlongitude!=0.0){
            binding.locationTv.setText(currentAddress);
        }*/




        loadCategories();
        loaAds("All");

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                Log.d(TAG,"onTextChanged:Query"+s);
                try{
                    String query =s.toString();
                    adapterAd.getFilter().filter(query);

                }catch (Exception e){
                    Log.e(TAG,"onTextChanged",e);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                //Intent intent = new Intent(hContext,LocationP)
            }
        });
    }

    private void loadCategories(){
        ArrayList<ModelCategory> categoryArrayList = new ArrayList<>();
        ModelCategory modelCategoryAll=new ModelCategory("All",R.drawable.all);

        categoryArrayList.add(modelCategoryAll);

        for(int i=0;i< MyUtils.categories.length;i++){
            ModelCategory modelCategory= new ModelCategory(MyUtils.categories[i],MyUtils.categoryIcon[i]);
            categoryArrayList.add(modelCategory);
        }

        AdapterCategory adapterCategory = new AdapterCategory(hContext, categoryArrayList, new RvListenerCategory() {
            @Override
            public void onCategoryClick(ModelCategory modelCategory) {

                loaAds(modelCategory.getCategory());
            }
        });

        binding.categoriesRv.setAdapter(adapterCategory);
    }

    private void loaAds(String category){

        Log.d(TAG,"loadAds:Category:"+category);
        adArrayList =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelAd modelAd =ds.getValue(ModelAd.class);
                    if(category.equals("All")){
                        adArrayList.add(modelAd);
                    }else{
                        if(modelAd.getCategory().equals(category)){
                            adArrayList.add(modelAd);
                        }
                    }
                }
                adapterAd =new AdapterAd(hContext,adArrayList);
                binding.adsRv.setAdapter(adapterAd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}