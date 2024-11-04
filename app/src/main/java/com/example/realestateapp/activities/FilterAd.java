package com.example.realestateapp.activities;

import android.widget.Adapter;
import android.widget.Filter;

import java.util.ArrayList;

public class FilterAd extends Filter {

    private AdapterAd adapter;
    private ArrayList<ModelAd> filterList;

    public FilterAd(AdapterAd adapter, ArrayList<ModelAd> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;

    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results=new FilterResults();
        if(charSequence!=null&&charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelAd> filteredModels = new ArrayList<>();
            for(int i =0;i<filterList.size();i++){
                if(filterList.get(i).getBrand().toUpperCase().contains(charSequence)||
                filterList.get(i).getCategory().toUpperCase().contains(charSequence)||
                        filterList.get(i).getCondition().toUpperCase().contains(charSequence)||
                        filterList.get(i).getTitle().toUpperCase().contains(charSequence)){
                    filteredModels.add(filterList.get(i));
                }

            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }else{
            results.count = filterList.size();
            results.values =filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.adArrayList =(ArrayList<ModelAd>)  filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
