package com.example.realestateapp.activities;

import android.content.Context;
import android.widget.Toast;
import android.text.format.DateFormat;

import com.example.realestateapp.R;

import java.util.Calendar;
import java.util.Locale;

public class MyUtils {

    public static final String USER_TYPE_GOOGLE = "Google";


    public static final String USER_TYPE_EMAIL = "Email";
    public static final String USER_TYPE_PHONE = "Phone";

    public static final String STATUS_AVAILABLE="AVAILABLE";
    public static final String STATUS_SOLD="SOLD";
    public static final String[] categories ={
            "Mobiles",
            "Computer/Laptop",
            "Vehicles",
            "Fashion & Beauty",
            "Books",
            "Sports"
    };

    public  static  final int[] categoryIcon={
            R.drawable.mobile,
            R.drawable.computer,
            R.drawable.car,
            R.drawable.face,
            R.drawable.book,
            R.drawable.sports,

    };

    public  static  final String[] conditions={
            "New",
            "Used",
            "Refurbished"
    };

    public static void toast(Context context, String mes){
        Toast.makeText(context,mes,Toast.LENGTH_SHORT).show();
    }

    public static long timestamp(){
        return System.currentTimeMillis();
    }
    public static String formatTimestampDate(Long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        return date;
    }
}
