package com.company.rss.rss.restful_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.Collection;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.FeedGrouping;
import com.company.rss.rss.models.Multifeed;
import com.company.rss.rss.models.SavedArticle;
import com.company.rss.rss.models.User;
import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.restful_api.callbacks.ArticleCallback;
import com.company.rss.rss.restful_api.callbacks.CollectionCallback;
import com.company.rss.rss.restful_api.callbacks.FeedCallback;
import com.company.rss.rss.restful_api.callbacks.FeedGroupCallback;
import com.company.rss.rss.restful_api.callbacks.MultifeedCallback;
import com.company.rss.rss.restful_api.callbacks.SavedArticleCallback;
import com.company.rss.rss.restful_api.callbacks.UserCallback;
import com.company.rss.rss.restful_api.interfaces.AsyncResponse;

import java.util.List;

/**
 * Retrieve User's Data from the API: Feeds, FeedGroups, Multifeeds, Collections and persist them
 */
public class LoadUserData extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "LoadUserData";

    //AsyncTask Results
    public static int DATA_LOADING_TERMINTAED   = 0;
    public static int AUTHENTICATION_FAILED     = 1;

    //Call back interface
    public AsyncResponse delegate = null;

    // The parent context
    private Context parent;

    // The User for which to gather all the info
    private User user;

    private RESTMiddleware api;
    private boolean userVerified;
    private boolean gotFeedGroups;
    private boolean gotFeeds;
    private boolean gotMultifeeds;
    private boolean gotCollections;
    private boolean gotSavedArticles;
    private boolean gotArticles;
    private boolean authFaild;

    /**
     * Constructor of LoadUserData class
     * @param asyncResponse AsyncTask response listener Callback
     * @param c             Activity Context
     * @param user          Logged User
     */
    public LoadUserData(AsyncResponse asyncResponse, Context c, User user){
        //Assigning call back interface through constructor
        delegate = asyncResponse;
        // Set the parent
        parent = c;
        // Set the user
        this.user = user;

        //Set booleans to default value
        userVerified    = false;
        gotFeedGroups   = false;
        gotFeeds        = false;
        gotMultifeeds   = false;
        gotCollections  = false;
        gotSavedArticles= false;
        gotArticles     = false;
        authFaild       = false;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(parent);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        //Get a SharedPreferences instance
        final UserPrefs prefs = new UserPrefs(parent);

        //Login Verification of the User
        Log.d(ArticleActivity.logTag + ":" + TAG, "Authenticating user...");
        api.getUserAuthentication(user.getEmail(), user.getPassword(), new UserCallback() {
            @Override
            public void onLoad(List<User> users) {
                userVerified = true;

                //Get logged user
                if(users.isEmpty() == false) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser authentication " + users.size());
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser: " + users.get(0).getId() +  ", " + users.get(0).getName() + ", "
                            + users.get(0).getSurname() + ", " + users.get(0).getEmail() + ", " + users.get(0).getPassword());
                }
                else {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Authentication Failed!");
                    //Get a SharedPreferences instance
                    //And Remove the User (since is wrong)
                    UserPrefs prefs = new UserPrefs( parent);
                    prefs.removeUser();
                    authFaild = true;
                }
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserAuthentication");
            }
        });

        //Get all the User's FeedGroups
        Log.d(ArticleActivity.logTag + ":" + TAG, "\ngetUserFeedGroups:");
        api.getUserFeedGroups(user.getId(), new FeedGroupCallback() {
            @Override
            public void onLoad(List<FeedGrouping> feedGroups) {
                for(FeedGrouping feedGroup:feedGroups) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nFeed: " + feedGroup.getFeed() + "," + feedGroup.getMultifeed()+ "," + feedGroup.getArticleCheckpoint());
                }

                //Persist the FeedGroups
                prefs.storeFeedGroups(feedGroups);
                gotFeedGroups = true;
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserFeedGroups");
            }
        });

        //Get all the User's Feeds
        Log.d(ArticleActivity.logTag + ":" + TAG, "\ngetUserFeeds:");
        api.getUserFeeds(user.getEmail(), new FeedCallback() {
            @Override
            public void onLoad(List<Feed> feeds) {
                for(Feed feed:feeds) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nFeed: " + feed.getTitle() + "," + feed.getLink()+ "," + feed.getCategory()+ "," + feed.getLang());
                }

                //Persist the Feeds
                prefs.storeFeeds(feeds);
                gotFeeds = true;
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserFeeds");
            }
        });

        //Gets the list of all the User's Multifeeds
        Log.d(ArticleActivity.logTag + ":" + TAG, "\ngetUserMultifeeds:");
        api.getUserMultifeeds(user.getEmail(), new MultifeedCallback() {
            @Override
            public void onLoad(List<Multifeed> multifeeds) {
                for(Multifeed multifeed:multifeeds) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nrMultifeed: " + multifeed.getTitle() + "," + multifeed.getUser()+ "," + multifeed.getColor());
                }

                //Persist the Multifeeds
                prefs.storeMultifeeds(multifeeds);
                gotMultifeeds = true;
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserMultifeeds");
            }
        });

        //Gets the list of all the User's Collections
        Log.d(ArticleActivity.logTag + ":" + TAG, "\ngetUserCollections:");
        api.getUserCollections(user.getEmail(), new CollectionCallback() {
            @Override
            public void onLoad(List<Collection> collections) {
                for(Collection collection:collections) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nCollection: " + collection.getTitle() + "," + collection.getUser()+ "," + collection.getColor());
                }

                //Persist the Collections
                prefs.storeCollections(collections);
                gotCollections = true;
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserCollections");
            }
        });

        //Gets the list of all the User's SavedArticles
        Log.d(ArticleActivity.logTag + ":" + TAG, "\ngetUserSavedArticles:");
        api.getUserSavedArticles(user.getId(), new SavedArticleCallback() {
            @Override
            public void onLoad(List<SavedArticle> savedArticles) {
                for(SavedArticle savedArticle:savedArticles) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nSavedArticle: " + savedArticle.getArticle() + "," + savedArticle.getCollection());
                }

                //Persist the SavedArticles
                prefs.storeSavedArticles(savedArticles);
                gotSavedArticles = true;
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserSavedArticles");
            }
        });

        //Gets the list of all the User's Articles by user id
        Log.d(ArticleActivity.logTag + ":" + TAG, "\ngetUserArticles:");
        api.getUserArticles(user.getId(), new ArticleCallback() {
            @Override
            public void onLoad(List<Article> articles) {
                for(Article article:articles) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nArticle: " + article.getHashId() + "," + article.getTitle()+ "," + article.getLink());
                }

                //Persist the Articles
                prefs.storeArticles(articles);
                gotArticles = true;
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserArticles");
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

        //Return result
        if(authFaild)
            return AUTHENTICATION_FAILED;
        else
            return DATA_LOADING_TERMINTAED;
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
        if(gotFeedGroups && gotFeeds && gotMultifeeds && gotCollections && gotSavedArticles && gotArticles && userVerified)
            return false;
        else
            return true;
    }
}