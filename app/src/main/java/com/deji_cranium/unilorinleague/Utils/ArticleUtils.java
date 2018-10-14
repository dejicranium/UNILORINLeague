package com.deji_cranium.unilorinleague.Utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.deji_cranium.unilorinleague.Article;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleUtils{
    private static Date cleanedPubDate;    //this is needed in the getDate instance method below;
    private static final DateFormat dateFormat;



    static {
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
    }



    private ArticleUtils(){
        //Assertion to ensure class noninstantiability;
        throw new AssertionError("Class not meant to be instantiated");
    }



    public static int getYear(String date){
        int year = 0;
        Pattern pattern = Pattern.compile("(\\w){4}"); //pattern to find the first 4-letter token

        Matcher matcher = pattern.matcher(date);

        //the loop breaks when the first 4-letter token is found (which is always the year);
        while (matcher.find()){
            year = Integer.valueOf(matcher.group());
            break;
        }

        return year;
    }

    public static String getDate(String date) throws ParseException {
       /** String calculatedDate = new String();
        int DAYS_IN_MILLIS = 1000*60*60*24;
        **/

        HashMap<Integer, String>months = new HashMap<>();
        months.put(0, "Jan");
        months.put(1, "Feb");
        months.put(2, "Mar");
        months.put(3, "Apr");
        months.put(4, "May");
        months.put(5, "Jun");
        months.put(6, "Jul");
        months.put(7, "Aug");
        months.put(8, "Sep");
        months.put(9, "Oct");
        months.put(10, "Nov");
        months.put(11, "Dec");


        try {
            cleanedPubDate = dateFormat.parse(date);
            cleanedPubDate.setYear(getYear(date));
        }
        catch (Exception e){
            return "none";
        }


        return months.get(cleanedPubDate.getMonth()) +" "+ cleanedPubDate.getDate()+", " + cleanedPubDate.getYear();
    }


}
