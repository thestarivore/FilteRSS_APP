package com.company.rss.rss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.company.rss.rss.adapters.FeedsListAdapter;
import com.company.rss.rss.fragments.MultifeedEditFragment;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;
import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.models.UserData;
import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;

import java.util.List;

public class MultifeedCreationActivity extends AppCompatActivity implements MultifeedEditFragment.MultifeedEditInterface {
    public static final String CREATED_MULTIFEED_EXTRA = "CREATED_MULTIFEED";
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private UserData userData;
    private UserPrefs prefs;


    private MultifeedEditFragment multifeedEditFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multifeed_creation);

        prefs = new UserPrefs(this);

        loadUserData();

        api = new RESTMiddleware(this);

        Toolbar toolbar = findViewById(R.id.multifeed_creation_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        multifeedEditFragment = MultifeedEditFragment.newInstance(null);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.multifeedEditFrameLayout, multifeedEditFragment);
        ft.commit();


    }

    private void loadUserData(){
        if(userData == null) {
            //Get a UserData instance
            userData = UserData.getInstance();
            userData.loadPersistedData(this);
            userData.processUserData();
        }
    }

    @Override
    public void onUpdateMultifeed(Multifeed multifeed) {
        // Do nothing because the user clicked on the back button
    }

    @Override
    public void onDeleteFeed(Multifeed multifeed, Feed feed, List<Feed> feeds, int position, FeedsListAdapter adapter) {
        // Do nothing because the list of feed is empty (we are creating a new multifeed)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_multifeed_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Back clicked
                finish();
                return true;
            case R.id.itemSaveMultifeed: // Save clicked
                createNewMultifeed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void createNewMultifeed() {
        // The user clicked on the save icon
        // get the multifeed from the fragment
        final Multifeed multifeed = multifeedEditFragment.getMultifeed();

        // Save the multifeed and finish
        final Intent returnIntent = new Intent();

        if(validMultifeed(multifeed)){

            Log.d(ArticleActivity.logTag + ":" + TAG, "Saving newly created multifeed, info: " + multifeed.toString());

            // Call the API and add the user
            api.addUserMultifeed(multifeed.getTitle(), userData.getUser().getId(), multifeed.getColor(), multifeed.getRating(), new SQLOperationCallback() {
                @Override
                public void onLoad(SQLOperation sqlOperation) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed saved successfully via API " + sqlOperation.toString());

                    // return with success code
                    multifeed.setId(sqlOperation.getInsertId());
                    returnIntent.putExtra(CREATED_MULTIFEED_EXTRA, multifeed);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }

                @Override
                public void onFailure() {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Multifeed not saved ");
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            });

        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }



    }

    public boolean validMultifeed(Multifeed multifeed) {
        boolean valid = true;

        String title = multifeed.getTitle();

        if (title.isEmpty()) {
            valid = false;
        }

        return valid;
    }
}
