package com.company.rss.rss.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.Collection;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.FeedGrouping;
import com.company.rss.rss.models.Multifeed;
import com.company.rss.rss.models.SavedArticle;
import com.company.rss.rss.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/** stores the user object in SharedPreferences */
public class UserPrefs{

    /** This application's preferences label */
    private static final String PREFS_NAME = "com.company.rss.rss.persistence.UserPrefs";

    /** JSON Object Labels */
    private static final String USER_OBJECT                 = "LoggedUser";
    private static final String FEEDGROUP_LIST_OBJECT       = "FeedGroupList";
    private static final String FEED_LIST_OBJECT            = "FeedList";
    private static final String MULTIFEED_LIST_OBJECT       = "MultifeedList";
    private static final String COLLECTION_LIST_OBJECT      = "CollectionList";
    private static final String SAVEDARTICLES_LIST_OBJECT   = "SavedArticlesList";
    private static final String ARTICLES_LIST_OBJECT        = "ArticleList";

    /** This application's preferences */
    private static SharedPreferences settings;

    /** This application's settings editor*/
    private static SharedPreferences.Editor editor;

    /**
     * Constructor takes an android.content.Context argument
     **/
    public UserPrefs(Context ctx){
        if(settings == null){
            settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE );
        }
        /*
         * Get a SharedPreferences editor instance.
         * SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        editor = settings.edit();
    }

    /***************************************************
     *                      STORE
     **************************************************/
    /**
     * Store a User in the Shared Preferences
     * @param user
     */
    public void storeUser(User user){
        // convert User object user to JSON format
        Gson gson = new Gson();
        String user_json = gson.toJson(user);

        // store in SharedPreferences
        editor.putString(USER_OBJECT, user_json);
        editor.commit();
    }

    /**
     * Store a FeedGroup List in the Shared Preferences
     * @param feedGroups a list of FeedGrouping Objects
     */
    public void storeFeedGroups(List<FeedGrouping> feedGroups){
        // convert to JSON format
        Gson gson = new Gson();
        String fg_json = gson.toJson(feedGroups);

        // store in SharedPreferences
        editor.putString(FEEDGROUP_LIST_OBJECT, fg_json);
        editor.commit();
    }

    /**
     * Store a Feed List in the Shared Preferences
     * @param feeds a list of Feed Objects
     */
    public void storeFeeds(List<Feed> feeds){
        // convert to JSON format
        Gson gson = new Gson();
        String f_json = gson.toJson(feeds);

        // store in SharedPreferences
        editor.putString(FEED_LIST_OBJECT, f_json);
        editor.commit();
    }

    /**
     * Store a Multifeed List in the Shared Preferences
     * @param multifeeds a list of Multifeed Objects
     */
    public void storeMultifeeds(List<Multifeed> multifeeds){
        // convert to JSON format
        Gson gson = new Gson();
        String mf_json = gson.toJson(multifeeds);

        // store in SharedPreferences
        editor.putString(MULTIFEED_LIST_OBJECT, mf_json);
        editor.commit();
    }

    /**
     * Store a Collection List in the Shared Preferences
     * @param collections a list of Collection Objects
     */
    public void storeCollections(List<Collection> collections){
        // convert to JSON format
        Gson gson = new Gson();
        String c_json = gson.toJson(collections);

        // store in SharedPreferences
        editor.putString(COLLECTION_LIST_OBJECT, c_json);
        editor.commit();
    }

    /**
     * Store a SavedArticle List in the Shared Preferences
     * @param savedArticles a list of Collection Objects
     */
    public void storeSavedArticles(List<SavedArticle> savedArticles){
        // convert to JSON format
        Gson gson = new Gson();
        String sa_json = gson.toJson(savedArticles);

        // store in SharedPreferences
        editor.putString(SAVEDARTICLES_LIST_OBJECT, sa_json);
        editor.commit();
    }

    /**
     * Store a Article List in the Shared Preferences
     * @param articles a list of Collection Objects
     */
    public void storeArticles(List<Article> articles) {
        // convert to JSON format
        Gson gson = new Gson();
        String a_json = gson.toJson(articles);

        // store in SharedPreferences
        editor.putString(ARTICLES_LIST_OBJECT, a_json);
        editor.commit();
    }


    /***************************************************
     *                    RETRIEVE
     **************************************************/
    /**
     * Retrive a User from the Shared Preferences
     * @return User read
     */
    public User retrieveUser(){
        Gson gson = new Gson();
        String usersJson = settings.getString(USER_OBJECT, "");
        return gson.fromJson(usersJson, User.class);
    }

    /**
     * Retrive the FeedGroup List from the Shared Preferences
     * @return A list of FeedGrouping Objects
     */
    public List<FeedGrouping> retrieveFeedGroups(){
        Gson gson = new Gson();
        String feedGroupsJson = settings.getString(FEEDGROUP_LIST_OBJECT, "");
        Type type = new TypeToken<List<FeedGrouping>>(){}.getType();
        return gson.fromJson(feedGroupsJson, type);
    }

    /**
     * Retrive the Feed List from the Shared Preferences
     * @return A list of Feed Objects
     */
    public List<Feed> retrieveFeeds(){
        Gson gson = new Gson();
        String feedsJson = settings.getString(FEED_LIST_OBJECT, "");
        Type type = new TypeToken<List<Feed>>(){}.getType();
        return gson.fromJson(feedsJson, type);
    }

    /**
     * Retrive the Multifeed List from the Shared Preferences
     * @return A list of Multifeed Objects
     */
    public List<Multifeed> retrieveMultifeeds(){
        Gson gson = new Gson();
        String multifeedsJson = settings.getString(MULTIFEED_LIST_OBJECT, "");
        Type type = new TypeToken<List<Multifeed>>(){}.getType();
        return gson.fromJson(multifeedsJson, type);
    }

    /**
     * Retrive the Collection List from the Shared Preferences
     * @return A list of Collection Objects
     */
    public List<Collection> retrieveCollections(){
        Gson gson = new Gson();
        String collectionsJson = settings.getString(COLLECTION_LIST_OBJECT, "");
        Type type = new TypeToken<List<Collection>>(){}.getType();
        return gson.fromJson(collectionsJson, type);
    }

    /**
     * Retrive the SavedArticle List from the Shared Preferences
     * @return A list of SavedArticle Objects
     */
    public List<SavedArticle> retrieveSavedArticles(){
        Gson gson = new Gson();
        String savedArticlesJson = settings.getString(SAVEDARTICLES_LIST_OBJECT, "");
        Type type = new TypeToken<List<SavedArticle>>(){}.getType();
        return gson.fromJson(savedArticlesJson, type);
    }

    /**
     * Retrive the Article List from the Shared Preferences
     * @return A list of Article Objects
     */
    public List<Article> retrieveArticles(){
        Gson gson = new Gson();
        String articlesJson = settings.getString(ARTICLES_LIST_OBJECT, "");
        Type type = new TypeToken<List<Article>>(){}.getType();
        return gson.fromJson(articlesJson, type);
    }

}