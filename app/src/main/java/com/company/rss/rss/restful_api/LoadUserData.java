package com.company.rss.rss.restful_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.FeedGrouping;
import com.company.rss.rss.models.RSSFeed;
import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.restful_api.callbacks.FeedGroupCallback;
import com.company.rss.rss.restful_api.interfaces.AsyncResponse;
import com.company.rss.rss.rss_parser.DOMParser;

import java.util.List;

/**
 * Loads User's Data: Feeds, FeedGroups, Multifeeds, Collections and persist them
 */
public class LoadUserData extends AsyncTask<Void, Void, Object> {
    private static final String TAG = "LoadUserData";

    //Call back interface
    public AsyncResponse delegate = null;

    // The parent context
    private Context parent;

    // The User Id for which to gather all the info
    private int userId;

    private RESTMiddleware api;

    public LoadUserData(AsyncResponse asyncResponse, Context c, int userId){
        //Assigning call back interface through constructor
        delegate = asyncResponse;
        // Set the parent
        parent = c;
        // Set the user id
        this.userId = userId;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(parent);
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Get a SharedPreferences instance
        final UserPrefs prefs = new UserPrefs(parent);

        //Get all the User's FeedGroups
        Log.d(TAG, "\ngetUserFeedGroups:");
        api.getUserFeedGroups(userId, new FeedGroupCallback() {
            @Override
            public void onLoad(List<FeedGrouping> feedGroups) {
                for(FeedGrouping feedGroup:feedGroups) {
                    Log.d(TAG, "\nFeed: " + feedGroup.getFeed() + "," + feedGroup.getMultifeed()+ "," + feedGroup.getArticleCheckpoint());
                }

                //Persist the FeedGroups
                prefs.storeFeedGroups(feedGroups);
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "\nFailure on: getUserFeedGroups");
            }
        });

        return null;
    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}