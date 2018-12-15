package com.company.rss.rss.models;

import android.content.Context;

import com.company.rss.rss.persistence.UserPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Class is the Model of all the User's Data collected in a single class,
 * the reason being to access and precess the data more easily
 */
public class UserData {
    //Logged User
    private User    user;

    //Unprocessed Data Lists
    private List<Feed>            feedList;
    private List<Multifeed>       multifeedList;
    private List<FeedGrouping>    feedGroupList;
    private List<Collection>      collectionList;

    //Maps
    private Map<Multifeed,List<Feed>>       multifeedMap;
    private Map<Collection,List<Article>>   collectionMap;

    /**
     * Simple Constructor
     */
    public UserData() {
        //Init of Hash Maps
        multifeedMap   = new HashMap<>();
        collectionMap  = new HashMap<>();
    }

    /**
     * Load all the User's persisted data, which includes:
     * - The logged User
     * - the list of User's Feed-Multifeed Groups
     * - The list of User's Feeds
     * - The list of User's Multifeeds
     * - The list of User's Collections
     * @param context Activity's context
     */
    public void loadPersistedData(Context context){
        //Get a SharedPreferences instance
        UserPrefs prefs = new UserPrefs(context);

        //Get the User data
        user  = prefs.retrieveUser();
        feedGroupList  = prefs.retrieveFeedGroups();
        feedList       = prefs.retrieveFeeds();
        multifeedList  = prefs.retrieveMultifeeds();
        collectionList = prefs.retrieveCollections();
    }

    /**
     * Process the User's Data and Build the class's Map. These associations are much clearer than the raw tables, which associations are
     * defined by a ER-Model and are not easy to correlate.
     */
    public void processUserData(){
        processMultifeedMap();
    }

    /**
     *  Process the User's Data and Build a Multifeed Map (Multifeed --> List<Feed>).
     *  It iterates all the user's multifeeds, and for each of them finds which feeds are
     *  associated.
     */
    public void processMultifeedMap(){
        //For every multifeed we must map the list of feeds
        for(Multifeed multifeed: multifeedList){
            List<Feed>  mfeedList = new ArrayList<>();      //Feed List to map to the multifeed
            int multifeedId = multifeed.getId();

            //For every Feed-Multifeed Group, find the associated feeds and build the list
            for(FeedGrouping feedGroup: feedGroupList){
                if(feedGroup.getMultifeed() == multifeedId){
                    mfeedList.add(getFeedById(feedGroup.getFeed()));
                }
            }

            //Add the FeedList associated Multifeed in the MultifeedMap
            multifeedMap.put(multifeed, mfeedList);
        }
    }

    /**
     * Search the Feed that has the needed ID and return it
     * @param feedId Feed's Id
     * @return Feed object with the required Id
     */
    private Feed getFeedById(int feedId) {
        for(Feed feed: feedList){
            if (feed.getId() == feedId)
                return feed;
        }
        return null;
    }

    /**
     * Get a Titles List of the feeds stored in the map with the desired key
     * @param multifeedKey Multifeed Key in the Multifeed Map
     * @return List<String> object
     */
    public List<String> getMapFeedTitlesListByKey(Multifeed multifeedKey){
        List<String> feedTitlesList = new ArrayList<>();

        //For each Feed in the FeedList with the key "multifeedKey"
        for (Feed feed: multifeedMap.get(multifeedKey)){
            feedTitlesList.add(feed.getTitle());
        }
        return feedTitlesList;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Feed> getFeedList() {
        return feedList;
    }

    public void setFeedList(List<Feed> feedList) {
        this.feedList = feedList;
    }

    public List<Multifeed> getMultifeedList() {
        return multifeedList;
    }

    public void setMultifeedList(List<Multifeed> multifeedList) {
        this.multifeedList = multifeedList;
    }

    public List<FeedGrouping> getFeedGroupList() {
        return feedGroupList;
    }

    public void setFeedGroupList(List<FeedGrouping> feedGroupList) {
        this.feedGroupList = feedGroupList;
    }

    public List<Collection> getCollectionList() {
        return collectionList;
    }

    public void setCollectionList(List<Collection> collectionList) {
        this.collectionList = collectionList;
    }

    public Map<Multifeed, List<Feed>> getMultifeedMap() {
        return multifeedMap;
    }

    public void setMultifeedMap(Map<Multifeed, List<Feed>> multifeedMap) {
        this.multifeedMap = multifeedMap;
    }

    public Map<Collection, List<Article>> getCollectionMap() {
        return collectionMap;
    }

    public void setCollectionMap(Map<Collection, List<Article>> collectionMap) {
        this.collectionMap = collectionMap;
    }
}
