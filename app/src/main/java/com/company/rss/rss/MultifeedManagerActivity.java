package com.company.rss.rss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.company.rss.rss.fragments.MultifeedEditFragment;
import com.company.rss.rss.fragments.MultifeedListFragment;
import com.company.rss.rss.models.Multifeed;

import java.util.ArrayList;

public class MultifeedManagerActivity extends AppCompatActivity implements MultifeedEditFragment.MultifeedEditInterface, MultifeedListFragment.OnMultifeedListListener {
    private boolean isTwoPane = false;
    private boolean editView = false;
    private ArrayList<Multifeed> multifeeds;
    private ActionBar actionbar;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multifeed_manager);

        Toolbar toolbar = findViewById(R.id.multifeed_manager_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        // drawerLayout = findViewById(R.id.drawer_layout_multifeed_manager);

        determinePaneLayout();

        // TODO: get user's multifeeds
        multifeeds = (ArrayList<Multifeed>) Multifeed.generateMockupMultifeeds(4);

        showListFragment();
    }

    private void showListFragment() {
        MultifeedListFragment multifeedListFragment = MultifeedListFragment.newInstance(multifeeds);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.multifeedListFrameLayout, multifeedListFragment);
        ft.commit();
    }

    private void determinePaneLayout() {
        FrameLayout fragmentItemDetail = (FrameLayout) findViewById(R.id.multifeedEditFrameLayout);
        if (fragmentItemDetail != null) {
            // large layout is used
            isTwoPane = true;
        }
    }

    @Override
    public void onMultifeedSelected(int position) {
        if (isTwoPane) { // single activity with multifeed and detail
            // Replace frame layout with correct edit fragment
            MultifeedEditFragment multifeedEditFragment = MultifeedEditFragment.newInstance(multifeeds.get(position));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.multifeedEditFrameLayout, multifeedEditFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            // Update the list
            showListFragment();
        } else { // separate activities
            editView = true;
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            MultifeedEditFragment multifeedEditFragment = MultifeedEditFragment.newInstance(multifeeds.get(position));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.multifeedListFrameLayout, multifeedEditFragment);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(ArticleActivity.logTag, "onBackPressed Called");
        if(editView){
            showListFragment();
            editView = false;
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveMultifeed(Multifeed multifeed) {
        // TODO: call the API and update the multifeed
        Log.v(ArticleActivity.logTag, "Saving multifeed to API: " + multifeed.toString());
    }

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

