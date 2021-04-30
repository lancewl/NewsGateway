package com.example.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String STORY_DATA = "STORY_DATA";

    private final HashMap<String, Source> sourceMap = new HashMap<>();
    private final List<String> sourceList = new ArrayList<>();
    private final List<String> categoryList = new ArrayList<>();

    private Menu menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> sourceAdapter;
    private NewsReceiver newsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsReceiver = new NewsReceiver(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectDrawerItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        // Set up the adapter
        sourceAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceList);
        mDrawerList.setAdapter(sourceAdapter);

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        doSourceDownload("all");
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(newsReceiver);
        super.onStop();
    }

    // You need the 2 below to make the drawer-toggle work properly:
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        doSourceDownload(item.getTitle().toString());
        return super.onOptionsItemSelected(item);
    }

    private void makeMenu() {
        menu.clear();
        for (int i = 0; i < categoryList.size(); i++) {
            menu.add(categoryList.get(i));
        }
    }

    private void selectDrawerItem(int position) {
        String selectedSource = sourceList.get(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void doSourceDownload(String category) {
        SourceDownloadRunnable loaderTaskRunnable = new SourceDownloadRunnable(this, category);
        new Thread(loaderTaskRunnable).start();
    }

    public void setSource(HashMap<String, Source> sMap, List<String> cList) {
        sourceMap.clear();
        sourceMap.putAll(sMap);

        ArrayList<String> tempList = new ArrayList<>(sourceMap.keySet());
        Collections.sort(tempList);
        sourceList.clear();
        sourceList.addAll(tempList);
        sourceAdapter.notifyDataSetChanged();

        if (categoryList.isEmpty()) {
            categoryList.add("all");
            categoryList.addAll(cList);
            makeMenu();
        }
    }

    private class NewsReceiver extends BroadcastReceiver {

        private static final String TAG = "ServiceReceiver";
        private final MainActivity mainActivity;

        public NewsReceiver(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action == null)
                return;

            switch (action) {
                case ACTION_NEWS_STORY:
                    ArrayList<Story> storyList = null;

                    if (intent.hasExtra(STORY_DATA))
                        storyList = (ArrayList<Story>) intent.getSerializableExtra(STORY_DATA);

                    if (storyList != null) {
                        // Article download
                        Log.d(TAG, "onReceive: Story List broadcast received");
                    }
                    break;

                default:
                    Log.d(TAG, "onReceive: Unknown broadcast received");
            }
        }
    }
}