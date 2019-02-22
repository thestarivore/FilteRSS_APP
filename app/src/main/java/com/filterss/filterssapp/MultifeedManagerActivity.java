package com.filterss.filterssapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.filterss.filterssapp.adapters.FeedsListAdapter;
import com.filterss.filterssapp.adapters.MultifeedListAdapter;
import com.filterss.filterssapp.fragments.MultifeedEditFragment;
import com.filterss.filterssapp.fragments.MultifeedListFragment;
import com.filterss.filterssapp.models.Feed;
import com.filterss.filterssapp.models.Multifeed;
import com.filterss.filterssapp.models.SQLOperation;
import com.filterss.filterssapp.models.UserData;
import com.filterss.filterssapp.restful_api.RESTMiddleware;
import com.filterss.filterssapp.restful_api.callbacks.SQLOperationCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultifeedManagerActivity extends AppCompatActivity implements MultifeedEditFragment.MultifeedEditInterface, MultifeedListFragment.MultifeedListInterface {
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private boolean isTwoPane = false;
    private boolean editView = false;
    private ActionBar actionbar;
    private UserData userData;
    private ArrayList<Multifeed> multifeeds;
    private boolean multifeedsChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multifeed_manager);

        Toolbar toolbar = findViewById(R.id.multifeed_manager_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.multifeeds);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        loadUserData();

        api = new RESTMiddleware(this);
        multifeedsChange = false;


        // drawerLayout = findViewById(R.id.drawer_layout_multifeed_manager);

        determinePaneLayout();

        Map<Multifeed, List<Feed>> multifeedsMap = userData.getMultifeedMap();
        multifeeds = new ArrayList<Multifeed>();
        multifeeds.clear();

        for (Multifeed multifeed : multifeedsMap.keySet()) {
            multifeed.setFeeds(multifeedsMap.get(multifeed));
            multifeeds.add(multifeed);
        }

        showListFragment();
    }

    private void loadUserData() {
        if (userData == null) {
            //Get a UserData instance
            userData = UserData.getInstance();
            userData.loadPersistedData(this);
            userData.processUserData();
        }
    }

    private void showListFragment() {
        MultifeedListFragment multifeedListFragment = MultifeedListFragment.newInstance(multifeeds);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.multifeedListFrameLayout, multifeedListFragment);
        ft.commit();
    }

    /**
     * isTwoPane is used to determine if master-detail is used or not
     */
    private void determinePaneLayout() {
        FrameLayout fragmentItemDetail = findViewById(R.id.multifeedEditFrameLayout);
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
            actionbar.setTitle(multifeeds.get(position).getTitle());
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed passed: " + multifeeds.get(position));

            MultifeedEditFragment multifeedEditFragment = MultifeedEditFragment.newInstance(multifeeds.get(position));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.multifeedListFrameLayout, multifeedEditFragment);
            ft.commit();
        }
    }


    /**
     * The function is called by the MultifeedEditFragment when the multifeed is updated
     *
     * @param multifeed the updated multifeed
     */
    @Override
    public void onUpdateMultifeed(final Multifeed multifeed) {
        // TODO: call the API and update the multifeed
        Log.d(ArticleActivity.logTag + ":" + TAG, "Saving multifeed to API: " + multifeed.toString());
        api.updateUserMultifeed(multifeed.getId(), multifeed.getTitle(), multifeed.getColor(), multifeed.getRating(), new SQLOperationCallback() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed " + multifeed.getTitle() + " updated");
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_updated_successfully, Snackbar.LENGTH_LONG).show();
                multifeedsChange = true;
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Multifeed " + multifeed.getTitle() + "NOT updated");
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_update_error, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * The function is called when a multifeed is deleted. When the API returns a successful
     * response the multifeed is removed from the list
     *
     * @param multifeed  the removed multifeed
     * @param multifeeds the list of multifeeds that need to be updated
     * @param position   the position of the multifeed to remove
     * @param adapter    the adapter set on the list of multifeed
     */
    @Override
    public void onDeleteMultifeed(final Multifeed multifeed, final ArrayList<Multifeed> multifeeds, final int position, final MultifeedListAdapter adapter) {
        api.deleteUserMultifeed(multifeed.getId(), new SQLOperationCallback() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed " + multifeed.getTitle() + " deleted");
                multifeeds.remove(position);
                adapter.notifyDataSetChanged();
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_deleted_successfully, Snackbar.LENGTH_LONG).show();
                multifeedsChange = true;
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Multifeed " + multifeed.getTitle() + "NOT delete");
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_deletion_error, Snackbar.LENGTH_LONG).show();
            }
        });


    }


    /**
     * The function is called when a feed is deleted from a multifeed. When the API returns a successful
     * response the feed is removed from the list
     *
     * @param multifeed the multifeed from which the feed is removed
     * @param feed      the removed feed
     * @param feeds     the list of feeds that need to be updated
     * @param position  the position of the feed to remove
     * @param adapter   the adapter set on the list of feed
     */
    @Override
    public void onDeleteFeed(final Multifeed multifeed, final Feed feed, final List<Feed> feeds, final int position, final FeedsListAdapter adapter) {
        api.deleteUserFeed(feed.getId(), multifeed.getId(), new SQLOperationCallback() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Feed " + feed.getTitle() + " deleted from Multifeed " + multifeed.getTitle());
                feeds.remove(position);     // remove the feed from the feeds list
                multifeed.setFeeds(feeds);  // to have a consistent number of feeds
                adapter.updateFeeds(feeds); // update the adapter
                Snackbar.make(findViewById(android.R.id.content), R.string.feed_deleted_successfully, Snackbar.LENGTH_LONG).show();
                multifeedsChange = true;
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Feed " + feed.getTitle() + "NOT delete from Multifeed " + multifeed.getTitle());
                Snackbar.make(findViewById(android.R.id.content), R.string.feed_deletion_error, Snackbar.LENGTH_LONG).show();
            }
        });
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

    @Override
    public void onBackPressed() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Back pressed...");
        if (editView) {
            showListFragment();
            editView = false;
            actionbar.setTitle(R.string.multifeeds);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        } else {
            if (multifeedsChange) {
                // Return RESULT_OK to notify that data changed
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

}

