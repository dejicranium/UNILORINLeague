package com.deji_cranium.unilorinleague.Utils;

/**
 * Created by cranium on 10/30/17.
 */

public class HtmlUtils {

    private HtmlUtils(){
        //Assertion to ensure class's noninstantiability;
        throw new AssertionError("Class not meant to be instantiated");
    }

    public static String changeTopScorersTable(String html){
        String head = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" />" +
                "<style>" +
                "img{height:30px; width:30px;}"+
                "table{font-family:arial, sans-serif; border-collapse: collapse; margin:0px, 0px; 10px; 0px; width:100%; height:75%}"+
                "th, td {border: 1px solid #dddddd;}"+
                "th{text-align:left}"+
                "tr:nth-child(even){background-color: #dddddd;}"+
                "</style>"+
                "</head><body>";
        String closeTag = "</body></html>";
        String changedHtml = head + html + closeTag;
        return changedHtml;

    }

    public static String changeNormalTable(String html){
        String head = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" />" +
                "<style>" +
                "img{height:30px; width:30px;}"+
                "table{font-family:arial, sans-serif; border-collapse: collapse; margin:0px, 0px; 10px; 0px; width:100%; height:75%}"+
                "th, td {border: 1px solid #dddddd;}"+
                "th{text-align:left}"+
                "tr:nth-child(even){background-color: #dddddd;}"+
                "</style>"+
                "</head><body>";
        String closeTag = "</body></html>";
        String changedHtml = head + html + closeTag;
        return changedHtml;

    }


    public static String changeHtml(String html){
        String head = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" />" +
                "<style>" +
                "table{font-family:arial, sans-serif; border-collapse: collapse;}"+
                "th, td {border: 1px solid #dddddd;}"+
                "th{text-align:left}"+
                "tr:nth-child(even){background-color: #dddddd;}"+
                "</style>"+
                "</head><body><p align='justify'>";

        String closeTag = "</p></body></html>";
        String changedHtml = head + html + closeTag;
        return changedHtml;
    }


    public static String changeContentHtml(String html){
        String head = "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" />" +
                "<style>" +
                "p{text-align:justify; line-height:30px; font-weight:500}" +
                "a{color:#FF9BA2F7;}"+

                "</style>"+
                "</head><body><p>";


        String closeTag = "</p></body></html>";
        String changedHtml = head + html + closeTag;
        return changedHtml;

    }
}

