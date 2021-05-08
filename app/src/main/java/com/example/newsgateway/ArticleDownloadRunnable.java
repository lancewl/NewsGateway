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

import javax.net.ssl.HttpsURLConnection;

public class ArticleDownloadRunnable implements Runnable {

    private static final String TAG = "ArticleDownloadRunnable";

    private final NewsService newsService;
    private final String source;

    private static final String sourceURL = "https://newsapi.org/v2/top-headlines";
    private static final String yourAPIKey = "5acbea8dcd68465283def983f1f41f34";

    ArticleDownloadRunnable(NewsService newsService, String source) {
        this.newsService = newsService;
        this.source = source;
    }

    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(sourceURL).buildUpon();
        buildURL.appendQueryParameter("sources", source);
        buildURL.appendQueryParameter("language", "en");
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
        final ArrayList<Article> articleList = getStoryList(jsonString);
        newsService.serArticles(articleList);
    }

    private ArrayList<Article> getStoryList(String s) {
        ArrayList<Article> articleList = new ArrayList<>();

        try {
            JSONObject returnObject = new JSONObject(s);
            JSONArray articleArray = returnObject.getJSONArray("articles");

            for(int i=0; i<articleArray.length(); i++) {
                JSONObject articleObject = articleArray.getJSONObject(i);

                String author = articleObject.getString("author");
                String title = articleObject.getString("title");
                String description = articleObject.getString("description");
                String url = articleObject.getString("url");
                String urlToImage = articleObject.getString("urlToImage");
                String publishedAt = articleObject.getString("publishedAt");

                Article article = new Article(author, title, description, url, urlToImage, publishedAt);

                articleList.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return articleList;
    }
}
