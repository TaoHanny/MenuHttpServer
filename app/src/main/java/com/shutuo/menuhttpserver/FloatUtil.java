package com.shutuo.menuhttpserver;

import android.annotation.SuppressLint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FloatUtil {


    public static int returnActualLength(byte[] data) {
        int i = 0;
        for (; i < data.length; i++) {
            if (data[i] == '\0')
                break;
        }
        return i;
    }

    public static int getStringToInt(String unit){
        if(unit.length()==0) return 1;
        String reg = "[^0-9]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(unit);
//        String countStr= unit.charAt(0)+"";
        String countStr= m.replaceAll("").trim();
//        Log.e("AAAAA", "getStringToInt()  countStr = "+countStr);
        return Integer.parseInt(countStr);
    }

    public static double getStringToDouble(String  foodAmount) {
        double foodAmountDouble = Double.parseDouble(foodAmount);
        @SuppressLint("DefaultLocale")
        Double get_double = Double.parseDouble(String.format("%.2f", foodAmountDouble));
        return get_double.doubleValue();
    }

    public static double getFloatScale(double foodAmount) {

        @SuppressLint("DefaultLocale")
        Double get_double = Double.parseDouble(String.format("%.2f", foodAmount));

        return get_double.doubleValue();
    }
} 