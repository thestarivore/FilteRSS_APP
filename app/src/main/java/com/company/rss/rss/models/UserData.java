package com.company.rss.rss.models;

import android.content.Context;

import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.persistence.articles.ArticleCursor;
import com.company.rss.rss.persistence.articles.ArticleSQLiteRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Class is the Model of all the User's Data collected in a single class,
 * the reason being to access and precess the data more easily
 */
public class UserData {
    //Singleton Instance
    private static final UserData ourInstance = new UserData();

    //Logged User
    private User    user;

    //Unprocessed Data Lists
    private List<Feed>              feedList;
    private List<Multifeed>         multifeedList;
    private List<FeedGrouping>      feedGroupList;      //Feed-Multifeed connections
    private List<Collection>        collectionList;
    private List<SavedArticle>      savedArticleList;   //Article-Collection connections
    private List<Article>           articleList;        //User's saved/read articles
    //private List<Article>           dlArticleList;      //Downloaded Article List (The List of Articles that is actually downloaded from each Feed
                                                        //and later stored on a local SQLite Database)

    //Maps
    private Map<Multifeed,List<Feed>>       multifeedMap;
    private Map<Collection,List<Article>>   collectionMap;

    //AllFeedArticles, AllMultifeedArticles, FeedArticles, CollectionArticles ArticleList Visualization
    public static int MODE_ALL_MULTIFEEDS_FEEDS    = 0;
    public static int MODE_MULTIFEED_ARTICLES      = 1;
    public static int MODE_FEED_ARTICLES           = 2;
    public static int MODE_COLLECTION_ARTICLES     = 3;
    private int visualizationMode;
    private int multifeedPosition;
    private int feedPosition;
    private int collectionPosition;
    private UserPrefs prefs;
    private Context context;

    //SQLite
    private ArticleSQLiteRepository repository;

    /**
     * Get Singleton's instance
     * @return
     */
    public static UserData getInstance() {
        return ourInstance;
    }

    /**
     * Simple Constructor
     */
    public UserData() {
        //Init of Hash Maps
        multifeedMap   = new HashMap<>();
        collectionMap  = new HashMap<>();

        //Default visualization Mode
        visualizationMode = MODE_ALL_MULTIFEEDS_FEEDS;
    }

    /**
     * Load all the User's persisted data, which includes:
     * - The logged User
     * - the list of User's Feed-Multifeed Groups (FeedGroups)
     * - The list of User's Feeds
     * - The list of User's Multifeeds
     * - The list of User's Collections
     * - The list of User's Article-Collection Groups (SavedArticles)
     * - The list of User's Articles
     * @param context Activity's context
     */
    public void loadPersistedData(Context context){
        //Get a SharedPreferences instance
        this.context = context;
        prefs = new UserPrefs(context);

        //Get the User data
        user  = prefs.retrieveUser();
        feedGroupList   = prefs.retrieveFeedGroups();
        feedList        = prefs.retrieveFeeds();
        multifeedList   = prefs.retrieveMultifeeds();
        collectionList  = prefs.retrieveCollections();
        savedArticleList= prefs.retrieveSavedArticles();
        articleList     = prefs.retrieveArticles();

        //Initialize the SQLite Repository
        //repository = new ArticleSQLiteRepository(context);

        //Get all the downloaded articles from the SQLite local database
        //if(dlArticleList == null)
        //    dlArticleList   = getSQLStoredAllDownloadedArticles();
    }

    /**
     * Process the User's Data and Build the class's Map. These associations are much clearer than the raw tables, which associations are
     * defined by a ER-Model and are not easy to correlate.
     */
    public void processUserData(){
        processMultifeedMap();
        processCollectionMap();
    }

    /**
     *  Process the User's Data and Build a Multifeed Map (Multifeed --> List<Feed>).
     *  It iterates all the user's multifeeds, and for each of them finds which feeds are
     *  associated.
     */
    public void processMultifeedMap(){
        multifeedMap.clear();

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

            //For every feed in the list, assign the color of the multifeed
            for (Feed feed: mfeedList){
                feed.setMultifeed(multifeed);
            }

            //Add the FeedList associated Multifeed in the MultifeedMap
            multifeedMap.put(multifeed, mfeedList);
        }
    }

    /**
     *  Process the User's Data and Build a Collection Map (Collection --> List<Article>).
     *  It iterates all the user's multifeeds, and for each of them finds which feeds are
     *  associated.
     */
    public void processCollectionMap(){
        collectionMap.clear();

        //For every collection we must map the list of articles
        for(Collection collection: collectionList){
            List<Article>  articleList = new ArrayList<>();      //Article List to map to the collection
            int collectionId = collection.getId();

            //For every SavedArticle (Article-Collection association), find the associated articles and build the list
            for(SavedArticle savedArticle: savedArticleList){
                if(savedArticle.getCollection() == collectionId){
                    articleList.add(getArticleById(savedArticle.getArticle()));
                }
            }

            //Add the ArticleList associated Collection in the CollectionMap
            collectionMap.put(collection, articleList);
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
     * Search the Article that has the needed ID and return it
     * @param articleId Article's Id
     * @return Feed object with the required Id
     */
    private Article getArticleById(long articleId) {
        for (Article article: articleList){
            if (article.getHashId() == articleId)
                return article;
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

    /**
     * Get a Icon URLs List of the feeds stored in the map with the desired key
     * @param multifeedKey Multifeed Key in the Multifeed Map
     * @return List<String> object
     */
    public List<String> getMapFeedIconLinkListByKey(Multifeed multifeedKey){
        List<String> feedIconURLList = new ArrayList<>();

        //For each Feed in the FeedList with the key "multifeedKey"
        for (Feed feed: multifeedMap.get(multifeedKey)){
            feedIconURLList.add(feed.getVisualURL());
        }
        return feedIconURLList;
    }

    /**
     * Get a Titles List of the article stored in the map with the desired key
     * @param collectionKey Collection Key in the Collection Map
     * @return List<String> object
     */
    public List<String> getMapArticleTitlesListByKey(Collection collectionKey){
        List<String> articleTitlesList = new ArrayList<>();

        //For each Article in the ArticleList with the key "collectionKey"
        for (Article article: collectionMap.get(collectionKey)){
            articleTitlesList.add(article.getTitle());
        }
        return articleTitlesList;
    }

 /*   public Multifeed getFeedsMultifeed(Feed feed){
        for (Map.Entry<Multifeed, List<Feed>> entry : multifeedMap.entrySet()) {
            Multifeed multifeed = entry.getKey();
            List<Feed> feedList = entry.getValue();

        }
    }*/

   /* public void initSQLiteRepository(Context ctx){
        //Initialize the SQLite Repository
        repository = new ArticleSQLiteRepository(ctx);
    }

    public List<Article> getDownloadedArticleList() {
        return dlArticleList;
    }

    public void setDlArticleList(final List<Article> newDlArticleList) {
        this.dlArticleList = newDlArticleList;

        //Also persist the change
        Thread thread = new Thread() {
            @Override
            public void run() {
                setSQLStoredAllDownloadedArticles(newDlArticleList);
            }
        };
        thread.start();
    }

    public List<Article> getSQLStoredAllDownloadedArticles(){
        ArticleCursor cursor = repository.findAll();
        return getArticleListFromCursor(cursor);
    }

    public void setSQLStoredAllDownloadedArticles(List<Article> articles){
        for (Article article: articles){
            repository.add(article);
        }
    }

    public List<Article> getArticleListFromCursor(ArticleCursor cursor){
        final List<Article> articles = new ArrayList<>();

        //Iterate with the cursor and fill the list of articles
        while (cursor.moveToNext()){
            Article newArticle = new Article();
            newArticle.setHashId(cursor.getHashId());
            newArticle.setTitle(cursor.getTitle());
            newArticle.setAuthor(cursor.getAuthor());
            newArticle.setDescription(cursor.getDescription());
            newArticle.setComment(cursor.getComment());
            newArticle.setLink(cursor.getLink());
            newArticle.setImgLink(cursor.getImageLink());
            newArticle.setPubDateFromString(cursor.getPubDate());
            newArticle.setUser(cursor.getUser());
            newArticle.setFeed(cursor.getFeed());
            newArticle.setScore(cursor.getScore());
            articles.add(newArticle);
        }
        return articles;
    }*/

    /********************************************/
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

    public List<SavedArticle> getSavedArticleList() {
        return savedArticleList;
    }

    public void setSavedArticleList(List<SavedArticle> savedArticleList) {
        this.savedArticleList = savedArticleList;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public int getVisualizationMode() {
        return visualizationMode;
    }

    public void setVisualizationMode(int visualizationMode) {
        this.visualizationMode = visualizationMode;
    }

    public int getMultifeedPosition() {
        return multifeedPosition;
    }

    public void setMultifeedPosition(int multifeedPosition) {
        this.multifeedPosition = multifeedPosition;
    }

    public int getFeedPosition() {
        return feedPosition;
    }

    public void setFeedPosition(int feedPosition) {
        this.feedPosition = feedPosition;
    }

    public int getCollectionPosition() {
        return collectionPosition;
    }

    public void setCollectionPosition(int collectionPosition) {
        this.collectionPosition = collectionPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (user != null ? !user.equals(userData.user) : userData.user != null) return false;
        if (feedList != null ? !feedList.equals(userData.feedList) : userData.feedList != null)
            return false;
        if (multifeedList != null ? !multifeedList.equals(userData.multifeedList) : userData.multifeedList != null)
            return false;
        if (feedGroupList != null ? !feedGroupList.equals(userData.feedGroupList) : userData.feedGroupList != null)
            return false;
        if (collectionList != null ? !collectionList.equals(userData.collectionList) : userData.collectionList != null)
            return false;
        if (savedArticleList != null ? !savedArticleList.equals(userData.savedArticleList) : userData.savedArticleList != null)
            return false;
        if (articleList != null ? !articleList.equals(userData.articleList) : userData.articleList != null)
            return false;
        if (multifeedMap != null ? !multifeedMap.equals(userData.multifeedMap) : userData.multifeedMap != null)
            return false;
        return collectionMap != null ? collectionMap.equals(userData.collectionMap) : userData.collectionMap == null;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (feedList != null ? feedList.hashCode() : 0);
        result = 31 * result + (multifeedList != null ? multifeedList.hashCode() : 0);
        result = 31 * result + (feedGroupList != null ? feedGroupList.hashCode() : 0);
        result = 31 * result + (collectionList != null ? collectionList.hashCode() : 0);
        result = 31 * result + (savedArticleList != null ? savedArticleList.hashCode() : 0);
        result = 31 * result + (articleList != null ? articleList.hashCode() : 0);
        result = 31 * result + (multifeedMap != null ? multifeedMap.hashCode() : 0);
        result = 31 * result + (collectionMap != null ? collectionMap.hashCode() : 0);
        return result;
    }

    /**
     * Delete all the persisted data
     */
    public void deleteAll() {
        prefs = new UserPrefs(context);
        prefs.removeUser();
    }
}
