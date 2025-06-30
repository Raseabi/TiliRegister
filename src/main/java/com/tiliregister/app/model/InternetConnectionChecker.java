package com.tiliregister.app.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetConnectionChecker {
    public static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000); // 3 seconds timeout
            connection.connect();
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException e) {
            return false;
        }
    }
}
