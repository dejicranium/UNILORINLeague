package com.deji_cranium.unilorinleague.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cranium on 11/1/17.
 */

public class CustomScraper {

    URL url = new URL("http://facebook.com");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    String something = httpURLConnection.getContent().toString();


    public CustomScraper() throws IOException {
    }
}
