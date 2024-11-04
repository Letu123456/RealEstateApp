package com.example.realestateapp.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.example.realestateapp.databinding.RowAdBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterAd extends RecyclerView.Adapter<AdapterAd.HolderAd> implements Filterable {

    private FirebaseAuth firebaseAuth;
    private Context context;
    public ArrayList<ModelAd> adArrayList;
    private RowAdBinding binding;
    private static final String TAG="ADAPTER_AD_TAG";

    private FilterAd filter;
    private ArrayList<ModelAd> filterList;
    public AdapterAd(Context context, ArrayList<ModelAd> adArrayList) {
        this.context = context;
        this.adArrayList = adArrayList;
        this.filterList = adArrayList;
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderAd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowAdBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderAd(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAd holder, int position) {
ModelAd modelAd = adArrayList.get(position);

String title = modelAd.getTitle();
        String description = modelAd.getDescription();
        String address = modelAd.getAddress();
        String condition = modelAd.getCondition();
        String price = modelAd.getPrice();
        long timestamp = modelAd.getTimestamp();
        String formatDate = MyUtils.formatTimestampDate(timestamp);


        loadAdFirstImage(modelAd,holder);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.conditionTv.setText(condition);
        holder.priceTv.setText(price);
        holder.dateTv.setText(formatDate);
        holder.addressTv.setText(address);


    }

    private void loadAdFirstImage(ModelAd modelAd,HolderAd holder){
        Log.d(TAG,"loadFirstImage");
        String adId = modelAd.getId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
        reference.child(adId).child("Images").limitToFirst(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String imageUrl =""+ds.child("imageUrl").getValue();
                            Log.d(TAG,"ondatachange:imageUrl:"+imageUrl);
                            try{
                                Glide.with(context)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.image)
                                        .into(holder.imageTv);
                            }catch (Exception e){
                                Log.d(TAG,"onDataChange:",e);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }




    @Override
    public int getItemCount() {
        return adArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter ==null){
            filter=new FilterAd(this,filterList);

        }
        return filter;
    }

    class HolderAd extends RecyclerView.ViewHolder{

        ShapeableImageView imageTv;
        TextView titleTv,descriptionTv,addressTv,conditionTv,priceTv,dateTv;
        ImageButton favBtn;
        public HolderAd(@NonNull View itemView) {
            super(itemView);
            imageTv =binding.imageTv;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            favBtn=binding.favBtn;
            addressTv=binding.addressTv;
            conditionTv=binding.conditionTv;
            priceTv=binding.priceTv;
            dateTv=binding.dateTv;

        }
    }
}
