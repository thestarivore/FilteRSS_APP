package com.company.rss.rss.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.company.rss.rss.models.FeedGrouping;
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
    private static final String USER_OBJECT = "LoggedUser";
    private static final String FEEDGROUP_LIST_OBJECT = "FeedGroupList";

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
     * Retrive a User from the Shared Preferences
     * @return User read
     */
    public User retriveUser(){
        Gson gson = new Gson();
        String usersJson = settings.getString(USER_OBJECT, "");
        return gson.fromJson(usersJson, User.class);
    }

    /**
     * Store a FeedGroup List in the Shared Preferences
     * @param feedGroups a list of FeedGrouping Objects
     */
    public void storeFeedGroups(List<FeedGrouping> feedGroups){
        // convert User object user to JSON format
        Gson gson = new Gson();
        String fg_json = gson.toJson(feedGroups);

        // store in SharedPreferences
        editor.putString(FEEDGROUP_LIST_OBJECT, fg_json);
        editor.commit();
    }

    /**
     * Retrive the FeedGroup List from the Shared Preferences
     * @return A list of FeedGrouping Objects
     */
    public List<FeedGrouping> retriveFeedGroups(){
        Gson gson = new Gson();
        String feedGroupsJson = settings.getString(FEEDGROUP_LIST_OBJECT, "");
        Type type = new TypeToken<List<FeedGrouping>>(){}.getType();
        return gson.fromJson(feedGroupsJson, type);
    }
}