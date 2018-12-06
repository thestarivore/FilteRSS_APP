package com.company.rss.rss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.company.rss.rss.fragments.CollectionListFragment;
import com.company.rss.rss.models.Collection;

import java.util.ArrayList;

public class CollectionManagerActivity extends AppCompatActivity {
    private ArrayList<Collection> collections;
    private ActionBar actionbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_manager);

        Toolbar toolbar = findViewById(R.id.collection_manager_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        // TODO: get user's collections
        collections = (ArrayList<Collection>) Collection.generateMockupCollections(10);

        showListFragment();
    }

    private void showListFragment() {
        CollectionListFragment collectionListFragment = CollectionListFragment.newInstance(collections);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.collectionListFrameLayout, collectionListFragment);
        ft.commit();
    }

/*    @Override
    public void onSaveCollection(Collection collection) {
        // TODO: call the API and update the collection
        Log.v(ArticleActivity.logTag, "Saving multifeed to API: " + collection.toString());
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_multifeed_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            /*if(editView){
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }*/
        }
        return super.onOptionsItemSelected(item);
    }
}

