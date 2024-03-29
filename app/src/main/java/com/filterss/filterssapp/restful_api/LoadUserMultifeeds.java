package com.filterss.filterssapp.restful_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.filterss.filterssapp.models.Feed;
import com.filterss.filterssapp.models.FeedGrouping;
import com.filterss.filterssapp.models.Multifeed;
import com.filterss.filterssapp.models.User;
import com.filterss.filterssapp.persistence.UserPrefs;
import com.filterss.filterssapp.restful_api.callbacks.FeedCallback;
import com.filterss.filterssapp.restful_api.callbacks.FeedGroupCallback;
import com.filterss.filterssapp.restful_api.callbacks.MultifeedCallback;
import com.filterss.filterssapp.restful_api.interfaces.AsyncResponse;

import java.util.List;

/**
 * Retrieve User's Multifeed from the API: Feeds, FeedGroups, Multifeeds and persist them
 */
public class LoadUserMultifeeds extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "LoadUserMultifeeds";

    //Call back interface
    public AsyncResponse delegate = null;

    // The parent context
    private Context parent;

    // The User for which to gather all the info
    private User user;

    private RESTMiddleware api;
    private boolean gotFeedGroups;
    private boolean gotFeeds;
    private boolean gotMultifeeds;

    /**
     * Constructor of LoadUserData class
     * @param asyncResponse AsyncTask response listener Callback
     * @param c             Activity Context
     * @param user          Logged User
     */
    public LoadUserMultifeeds(AsyncResponse asyncResponse, Context c, User user){
        //Assigning call back interface through constructor
        delegate = asyncResponse;
        // Set the parent
        parent = c;
        // Set the user
        this.user = user;

        //Set booleans to default value
        gotFeedGroups   = false;
        gotFeeds        = false;
        gotMultifeeds   = false;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(parent);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        //Get a SharedPreferences instance
        final UserPrefs prefs = new UserPrefs(parent);

        //Get all the User's FeedGroups
        Log.d(TAG, "\ngetUserFeedGroups:");
        api.getUserFeedGroups(user.getId(), new FeedGroupCallback() {
            @Override
            public void onLoad(List<FeedGrouping> feedGroups) {
                for(FeedGrouping feedGroup:feedGroups) {
                    Log.d(TAG, "\nFeed: " + feedGroup.getFeed() + "," + feedGroup.getMultifeed()+ "," + feedGroup.getArticleCheckpoint());
                }

                //Persist the FeedGroups
                prefs.storeFeedGroups(feedGroups);
                gotFeedGroups = true;
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "\nFailure on: getUserFeedGroups");
            }
        });

        //Get all the User's Feeds
        Log.d(TAG, "\ngetUserFeeds:");
        api.getUserFeeds(user.getEmail(), new FeedCallback() {
            @Override
            public void onLoad(List<Feed> feeds) {
                for(Feed feed:feeds) {
                    Log.d(TAG, "\nFeed: " + feed.getTitle() + "," + feed.getLink()+ "," + feed.getCategory()+ "," + feed.getLang());
                }

                //Persist the Feeds
                prefs.storeFeeds(feeds);
                gotFeeds = true;
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "\nFailure on: getUserFeeds");
            }
        });

        //Gets the list of all the User's Multifeeds
        Log.d(TAG, "\ngetUserMultifeeds:");
        api.getUserMultifeeds(user.getEmail(), new MultifeedCallback() {
            @Override
            public void onLoad(List<Multifeed> multifeeds) {
                for(Multifeed multifeed:multifeeds) {
                    Log.d(TAG, "\nrMultifeed: " + multifeed.getTitle() + "," + multifeed.getUser()+ "," + multifeed.getColor());
                }

                //Persist the Multifeeds
                prefs.storeMultifeeds(multifeeds);
                gotMultifeeds = true;
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "\nFailure on: getUserMultifeeds");
            }
        });


        //Wait for all the API calls to return
        while (isStillDownloading()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }

    /**
     * Get the Global Downloading State
     * @return True if is still downloading False otherwise
     */
    private boolean isStillDownloading(){
        if(gotFeedGroups && gotFeeds && gotMultifeeds)
            return false;
        else
            return true;
    }
}