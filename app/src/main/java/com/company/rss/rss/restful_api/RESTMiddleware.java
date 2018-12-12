package com.company.rss.rss.restful_api;

import android.content.Context;

import com.company.rss.rss.restful_api.callbacks.ArticleCallback;
import com.company.rss.rss.restful_api.callbacks.CategoryCallback;
import com.company.rss.rss.restful_api.callbacks.CollectionCallback;
import com.company.rss.rss.restful_api.callbacks.FeedCallback;
import com.company.rss.rss.restful_api.callbacks.FeedGroupCallback;
import com.company.rss.rss.restful_api.callbacks.MultifeedCallback;
import com.company.rss.rss.restful_api.callbacks.ReadArticleCallback;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;
import com.company.rss.rss.restful_api.callbacks.UserCallback;
import com.company.rss.rss.service.RESTService;


public class RESTMiddleware {
    private Context context;

    /**
     * Simple Constructor
     */
    public RESTMiddleware() {
    }

    /**
     * Complete Constructor
     * @param context
     */
    public RESTMiddleware(Context context) {
        this.context = context;
    }


    /********************************************************************
     *                           Categories
     ********************************************************************/
    /**
     * Gets the list of all the Feed Categories
     * @param callback Callback for API response management
     */
    public void getAllCategories(CategoryCallback callback){
        RESTService.getInstance(context).getAllCategories(callback);
    }


    /********************************************************************
     *                           Auth
     ********************************************************************/
    /**
     * Gets the authenticated User
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void getUserAuthentication(String email, String password, UserCallback callback){
        RESTService.getInstance(context).getUserAuthentication(email, password, callback);
    }

    /**
     * Registration of a new User
     * @param name User's name
     * @param surname User's surname
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void registerNewUser(String name, String surname, String email, String password, SQLOperationCallback callback){
        RESTService.getInstance(context).registerNewUser(name, surname, email, password, callback);
    }

    /**
     * Change the password of the User's account
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void changeUsersPassword(String email, String password, SQLOperationCallback callback){
        RESTService.getInstance(context).changeUsersPassword(email, password, callback);
    }

    /********************************************************************
     *                           Feeds
     ********************************************************************/
    /**
     * Gets the list of all the Feeds
     * @param callback Callback for API response management
     */
    public void getAllFeeds(FeedCallback callback){
        RESTService.getInstance(context).getAllFeeds(callback);
    }

    /**
     * Gets the list of all the Filtered Feeds (Search)
     * @param searchFilter String containing the string pattern used to filter/search the desired feeds
     * @param callback Callback for API response management
     */
    public void getFilteredFeeds(String searchFilter, FeedCallback callback){
        RESTService.getInstance(context).getFilteredFeeds(searchFilter, callback);
    }

    /**
     * Create a new Feed
     * @param title Feed's title
     * @param url URL of the feed(not the website)
     * @param category Feed's Category
     * @param lang Language (Together with the category, they form a key in the Category table)
     * @param callback Callback for API response management
     */
    public void addFeed(String title, String url, String category, String lang, SQLOperationCallback callback){
        RESTService.getInstance(context).addFeed(title, url, category, lang, callback);
    }


    /********************************************************************
     *                        User - Feeds
     ********************************************************************/
    /**
     * Gets the list of all the User's Feeds
     * @param callback Callback for API response management
     */
    public void getUserFeeds(String userEmail, FeedCallback callback){
        RESTService.getInstance(context).getUserFeeds(userEmail, callback);
    }

    /**
     * Gets the list of all the User's FeedGroups
     * @param callback Callback for API response management
     */
    public void getUserFeedGroups(int userId, final FeedGroupCallback callback){
        RESTService.getInstance(context).getUserFeedGroups(userId, callback);
    }

    /**
     * Gets the article Checkpoint of the User's Feed-Multifeed Group
     * @param feed
     * @param multifeed
     * @param callback Callback for API response management
     */
    public void getUserFeedCheckpoint(int feed, int multifeed, ArticleCallback callback){
        RESTService.getInstance(context).getUserFeedCheckpoint(feed, multifeed, callback);
    }

    /**
     * Add a Feed to a User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback Callback for API response management
     */
    public void addUserFeed(int feed, int multifeed, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserFeed(feed, multifeed, callback);
    }

    /**
     * Add an article checkpoint to the Feed associated with the User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param article Article hashId, representing the check point of the feed associated with a user and multifeed
     * @param callback Callback for API response management
     */
    public void addUserFeedCheckpoint(int feed, int multifeed, long article, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserFeedCheckpoint(feed, multifeed, article, callback);
    }

    /**
     * Delete a Feed related to a User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback Callback for API response management
     */
    public void deleteUserFeed(int feed, int multifeed, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserFeed(feed, multifeed, callback);
    }


    /********************************************************************
     *                       User - Multifeeds
     ********************************************************************/
    /**
     * Gets the list of all the User's Multifeeds
     * @param userEmail Collection's owner email
     * @param callback Callback for API response management
     */
    public void getUserMultifeeds(String userEmail, MultifeedCallback callback){
        RESTService.getInstance(context).getUserMultifeeds(userEmail, callback);
    }

    /**
     * Add a Multifeed to a User
     * @param title Title of the Multifeed
     * @param user Owner of the multifeed
     * @param color Color used to indicate the multifeed on user's app
     * @param callback Callback for API response management
     */
    public void addUserMultifeed(String title, int user, int color, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserMultifeed(title, user, color, callback);
    }

    /**
     * Delete a Multifeed related to a User
     * @param id Multifeed's identification number
     * @param callback Callback for API response management
     */
    public void deleteUserMultifeed(int id, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserMultifeed(id, callback);
    }

    /********************************************************************
     *                       User - Collections
     ********************************************************************/
    /**
     * Gets the list of all the User's Collections
     * @param userEmail Colelction's owner email
     * @param callback Callback for API response management
     */
    public void getUserCollections(String userEmail, CollectionCallback callback){
        RESTService.getInstance(context).getUserCollections(userEmail, callback);
    }

    /**
     * Add a Collection to a User
     * @param title Title of the Collection
     * @param user Owner of the Collection
     * @param color Color used to indicate the Collection on user's app
     * @param callback Callback for API response management
     */
    public void addUserCollection(String title, int user, int color, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserCollection(title, user, color, callback);
    }

    /**
     * Delete a Collection related to a User
     * @param id Collection's identification number
     * @param callback Callback for API response management
     */
    public void deleteUserCollection(int id, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserCollection(id, callback);
    }


    /********************************************************************
     *                       User - Articles
     ********************************************************************/
    /**
     * Gets the list of all the User's Articles
     * @param feed
     * @param callback Callback for API response management
     */
    public void getUserArticles(int feed, ArticleCallback callback){
        RESTService.getInstance(context).getUserArticles(feed, callback);
    }

    /**
     * Add an Article to a User
     * @param title
     * @param description
     * @param comment
     * @param link
     * @param img_link
     * @param pub_date
     * @param user
     * @param feed
     * @param callback Callback for API response management
     */
    public void addUserArticle(String title, String description, String comment, String link, String img_link,
                               String pub_date, int user, int feed, SQLOperationCallback callback){
          RESTService.getInstance(context).addUserArticle(title, description, comment, link, img_link, pub_date, user, feed, callback);
    }

    /**
     * Delete a Article related to a User
     * @param hashId
     * @param callback Callback for API response management
     */
    public void deleteUserArticle(long hashId, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserArticle(hashId, callback);
    }


    /********************************************************************
     *                       User - SavedArticles
     ********************************************************************/
    /**
     * Gets the list of all the User's SavedArticles
     * @param id
     * @param callback Callback for API response management
     */
    public void getUserSavedArticles(int id, ArticleCallback callback){
        RESTService.getInstance(context).getUserSavedArticles(id, callback);
    }

    /**
     * Add an SavedArticle to the User
     * @param article
     * @param collection
     * @param callback Callback for API response management
     */
    public void addUserSavedArticle(long article, int collection, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserSavedArticle(article, collection, callback);
    }

    /**
     * Delete a SavedArticle related to a User
     * @param article
     * @param collection
     * @param callback Callback for API response management
     */
    public void deleteUserSavedArticle(long article, int collection, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserSavedArticle(article, collection, callback);
    }


    /********************************************************************
     *                       User - ReadArticles
     ********************************************************************/
    /**
     * Gets the list of all the User's ReadArticles
     * @param user
     * @param callback Callback for API response management
     */
    public void getUserReadArticles(int user, ReadArticleCallback callback){
        RESTService.getInstance(context).getUserReadArticles(user, callback);
    }

    /**
     * Add an ReadArticle to the User
     * @param user
     * @param article
     * @param vote
     * @param callback Callback for API response management
     */
    public void addUserReadArticle(int user, long article, int vote, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserReadArticle(user, article, vote, callback);
    }

    /**
     * Delete a ReadArticle related to a User
     * @param user
     * @param article
     * @param callback Callback for API response management
     */
    public void deleteUserReadArticle(int user, long article, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserReadArticle(user, article, callback);
    }
}