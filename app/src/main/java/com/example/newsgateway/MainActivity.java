package com.example.newsgateway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<Source> sourceList = new ArrayList<>();
    private final HashMap<String, Source> sourceMap = new HashMap<String, Source>();
    private final List<String> categoryList = new ArrayList<>();

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doSourceDownload("all");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        doSourceDownload(item.getTitle().toString());
        return super.onOptionsItemSelected(item);
    }

    private void makeMenu() {
        menu.clear();
        for (int i = 0; i < categoryList.size(); i++) {
            menu.add(categoryList.get(i));
        }
    }

    private void doSourceDownload(String category) {
        SourceDownloadRunnable loaderTaskRunnable = new SourceDownloadRunnable(this, category);
        new Thread(loaderTaskRunnable).start();
    }

    public void setSource(List<Source> sList, List<String> cList) {
        sourceList.addAll(sList);
        if (categoryList.size() == 0) {
            categoryList.add("all");
            categoryList.addAll(cList);
            makeMenu();
        }
    }
}