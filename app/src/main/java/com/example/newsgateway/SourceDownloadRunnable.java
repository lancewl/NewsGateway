package com.example.newsgateway;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

public class SourceDownloadRunnable implements Runnable {

    private static final String TAG = "SourceDownloadRunnable";

    private final MainActivity mainActivity;
    private final String category;

    private static final String sourceURL = "https://newsapi.org/v2/sources?language=en&country=us";
    private static final String yourAPIKey = "5acbea8dcd68465283def983f1f41f34";

    SourceDownloadRunnable(MainActivity mainActivity, String category) {
        this.mainActivity = mainActivity;
        if (category.equals("all")) {
            this.category = "";
        } else {
            this.category = category;
        }
    }

    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(sourceURL).buildUpon();
        buildURL.appendQueryParameter("category", category);
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.addRequestProperty("User-Agent","");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    public void handleResults(final String jsonString) {
        final HashMap<String, Source> sourceMap = getSourceList(jsonString);
        final ArrayList<String> categoryList = getCategoryList(jsonString);
        mainActivity.runOnUiThread(() -> mainActivity.setSource(sourceMap, categoryList));
    }

    private HashMap<String, Source> getSourceList(String s) {
        HashMap<String, Source> sourceMap = new HashMap<>();

        try {
            JSONObject returnObject = new JSONObject(s);
            JSONArray sourceArray = returnObject.getJSONArray("sources");

            for(int i=0; i<sourceArray.length(); i++) {
                JSONObject sourceObject = sourceArray.getJSONObject(i);

                String id = sourceObject.getString("id");
                String name = sourceObject.getString("name");
                String category = sourceObject.getString("category");

                sourceMap.put(name, new Source(id, name ,category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sourceMap;
    }

    private ArrayList<String> getCategoryList(String s) {
        HashSet<String> categorySet = new HashSet<>();

        try {
            JSONObject returnObject = new JSONObject(s);
            JSONArray sourceArray = returnObject.getJSONArray("sources");

            for(int i=0; i<sourceArray.length(); i++) {
                JSONObject sourceObject = sourceArray.getJSONObject(i);

                String category = sourceObject.getString("category");

                categorySet.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<String>(categorySet);
    }
}
