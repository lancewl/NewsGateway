package com.example.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    private ServiceReceiver serviceReceiver;
    private final ArrayList<Article> articleList = new ArrayList<>();

    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String SOURCE_DATA = "SOURCE_DATA";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceReceiver = new ServiceReceiver(this);
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, filter);

        //Creating new thread for my service
        //ALWAYS write your long running tasks
        // in a separate thread, to avoid an ANR issue

        new Thread(() -> {
            while (running) {
                while (articleList.isEmpty()) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            sendArticleBroadcast();
            articleList.clear();
        }).start();

        return Service.START_STICKY;
    }

    private void sendArticleBroadcast() {
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        intent.putExtra(MainActivity.STORY_DATA, articleList);
        sendBroadcast(intent);
    }

    public void serArticles(ArrayList<Article> al) {
        articleList.clear();
        articleList.addAll(al);
    }

    @Override
    public void onDestroy() {
        // Unregister the receiver!
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();
    }

    private class ServiceReceiver extends BroadcastReceiver {

        private static final String TAG = "ServiceReceiver";
        private final NewsService newsService;

        public ServiceReceiver(NewsService newsService) {
            this.newsService = newsService;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action == null)
                return;

            switch (action) {
                case ACTION_MSG_TO_SERVICE:
                    Source source = null;

                    if (intent.hasExtra(SOURCE_DATA))
                        source = (Source) intent.getSerializableExtra(SOURCE_DATA);

                    if (source != null) {
                        Log.d(TAG, "onReceive: Source broadcast received");
                        ArticleDownloadRunnable loaderTaskRunnable = new ArticleDownloadRunnable(newsService, source.getId());
                        new Thread(loaderTaskRunnable).start();
                    }
                    break;

                default:
                    Log.d(TAG, "onReceive: Unknown broadcast received");
            }
        }
    }
}
