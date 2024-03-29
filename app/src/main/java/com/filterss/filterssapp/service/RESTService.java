package com.filterss.filterssapp.service;

import android.content.Context;

import com.filterss.filterssapp.models.Article;
import com.filterss.filterssapp.models.ArticlesScores;
import com.filterss.filterssapp.models.Category;
import com.filterss.filterssapp.models.Collection;
import com.filterss.filterssapp.models.Feed;
import com.filterss.filterssapp.models.FeedGrouping;
import com.filterss.filterssapp.models.Multifeed;
import com.filterss.filterssapp.models.ReadArticle;
import com.filterss.filterssapp.models.SQLOperation;
import com.filterss.filterssapp.models.SavedArticle;
import com.filterss.filterssapp.models.User;
import com.filterss.filterssapp.restful_api.callbacks.ArticleCallback;
import com.filterss.filterssapp.restful_api.callbacks.ArticlesScoresCallback;
import com.filterss.filterssapp.restful_api.callbacks.CategoryCallback;
import com.filterss.filterssapp.restful_api.callbacks.CollectionCallback;
import com.filterss.filterssapp.restful_api.callbacks.FeedCallback;
import com.filterss.filterssapp.restful_api.callbacks.FeedGroupCallback;
import com.filterss.filterssapp.restful_api.callbacks.JsonArrayCallback;
import com.filterss.filterssapp.restful_api.callbacks.MultifeedCallback;
import com.filterss.filterssapp.restful_api.callbacks.ReadArticleCallback;
import com.filterss.filterssapp.restful_api.callbacks.SQLOperationCallback;
import com.filterss.filterssapp.restful_api.callbacks.SQLOperationListCallback;
import com.filterss.filterssapp.restful_api.callbacks.SavedArticleCallback;
import com.filterss.filterssapp.restful_api.callbacks.UserCallback;
import com.filterss.filterssapp.restful_api.interfaces.ArticlesRESTInterface;
import com.filterss.filterssapp.restful_api.interfaces.AuthenticationRESTInterface;
import com.filterss.filterssapp.restful_api.interfaces.CategoryRESTInterface;
import com.filterss.filterssapp.restful_api.interfaces.FeedsRESTInterface;
import com.filterss.filterssapp.restful_api.interfaces.UserRESTInterface;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTService {

    private static RESTService instance;
    private CategoryRESTInterface       categoryRESTInterface;
    private AuthenticationRESTInterface authenticationRESTInterface;
    private FeedsRESTInterface          feedsRESTInterface;
    private UserRESTInterface           userRESTInterface;
    private final ArticlesRESTInterface articlesRESTInterface;

    private RESTService(Context context){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.110:3000/")                                         //In local Nicholas
                //.baseUrl("http://192.168.1.22:3000/")                                         //In local Eddy
                //.baseUrl("http://ec2-35-180-230-227.eu-west-3.compute.amazonaws.com:3000")      //On Amazon AWS
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        categoryRESTInterface       = retrofit.create(CategoryRESTInterface.class);
        authenticationRESTInterface = retrofit.create(AuthenticationRESTInterface.class);
        feedsRESTInterface          = retrofit.create(FeedsRESTInterface.class);
        userRESTInterface           = retrofit.create(UserRESTInterface.class);
        articlesRESTInterface       = retrofit.create(ArticlesRESTInterface.class);
    }

    public static synchronized RESTService getInstance(Context context){
        if (instance == null){
            instance = new RESTService(context);
        }
        return instance;
    }

    /*********************** Categories *********************************/

    /**
     * Gets the list of all the Feed Categories
     * @param callback Callback for API response management
     */
    public void getAllCategories(final CategoryCallback callback){
        final List<Category> categories = new ArrayList<>();
        final AtomicBoolean alreadyFailed = new AtomicBoolean(false);

        categoryRESTInterface.getCategories()
            .enqueue(new retrofit2.Callback<List<Category>>() {
                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                    for (Category category: response.body()) {
                        categories.add(category);
                    }

                    callback.onLoad(categories);
                }

                @Override
                public void onFailure(Call<List<Category>> call, Throwable t) {
                    if (!alreadyFailed.getAndSet(true)){
                        callback.onFailure();
                    }
                }
            });
    }


    /*********************** Auth *********************************/

    /**
     * Gets the authenticated User
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void getUserAuthentication(String email, String password, final UserCallback callback){
        final List<User> users = new ArrayList<>();

        authenticationRESTInterface.getUserAuthentication(email, password).enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                for (User user: response.body()) {
                    users.add(user);
                }
                callback.onLoad(users);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Registration of a new User
     * @param name User's name
     * @param surname User's surname
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void registerNewUser(String name, String surname, String email, String password, final SQLOperationCallback callback){
        authenticationRESTInterface.registerNewUser(name,surname, email, password).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Change the password of the User's account
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void changeUsersPassword(String email, String password, final SQLOperationCallback callback){
        authenticationRESTInterface.changeUsersPassword(email, password).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Manage the login with a Google account
     * @param token the token of the Google account
     * @param callback Callback for API response management
     */
    public void getUserAuthenticationGoogle(String token, final UserCallback callback){
        final List<User> users = new ArrayList<>();

        authenticationRESTInterface.getUserAuthenticationGoogle(token).enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                for (User user: response.body()) {
                    users.add(user);
                }
                callback.onLoad(users);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /*********************** Feeds *********************************/

    /**
     * Gets the list of all the Feeds
     * @param callback Callback for API response management
     */
    public void getAllFeeds(final FeedCallback callback){
        final List<Feed> feeds = new ArrayList<>();

        feedsRESTInterface.getAllFeeds()
                .enqueue(new retrofit2.Callback<List<Feed>>() {
                    @Override
                    public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                        for (Feed feed: response.body()) {
                            feeds.add(feed);
                        }

                        callback.onLoad(feeds);
                    }

                    @Override
                    public void onFailure(Call<List<Feed>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Gets the list of all the Filtered Feeds (Search)
     * @param searchFilter String containing the string pattern used to filter/search the desired feeds, if any
     * @param category String containing the cateogry, if any
     * @param callback Callback for API response management
     */
    public void getFilteredFeeds(String searchFilter, String category, final FeedCallback callback){
        final List<Feed> feeds = new ArrayList<>();

        if(searchFilter != null && category != null) {
            feedsRESTInterface.getFilteredFeedsBySearchAndCategory(searchFilter, category)
                    .enqueue(new retrofit2.Callback<List<Feed>>() {
                        @Override
                        public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                            for (Feed feed: response.body()) {
                                feeds.add(feed);
                            }

                            callback.onLoad(feeds);
                        }

                        @Override
                        public void onFailure(Call<List<Feed>> call, Throwable t) {
                            callback.onFailure();
                        }
                    });
            return;
        }

        if(searchFilter != null){
            feedsRESTInterface.getFilteredFeedsBySearch(searchFilter)
                    .enqueue(new retrofit2.Callback<List<Feed>>() {
                        @Override
                        public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                            for (Feed feed: response.body()) {
                                feeds.add(feed);
                            }

                            callback.onLoad(feeds);
                        }

                        @Override
                        public void onFailure(Call<List<Feed>> call, Throwable t) {
                            callback.onFailure();
                        }
                    });
            return;
        }


        if(category != null){
            feedsRESTInterface.getFilteredFeedsByCategory(category)
                    .enqueue(new retrofit2.Callback<List<Feed>>() {
                        @Override
                        public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                            for (Feed feed: response.body()) {
                                feeds.add(feed);
                            }

                            callback.onLoad(feeds);
                        }

                        @Override
                        public void onFailure(Call<List<Feed>> call, Throwable t) {
                            callback.onFailure();
                        }
                    });
            return;
        }
    }

    /**
     * Create a new Feed
     * @param title Feed's title
     * @param url URL of the feed(not the website)
     * @param category Feed's Category
     * @param lang Language (Together with the category, they form a key in the Category table)
     * @param callback Callback for API response management
     */
    public void addFeed(String title, String url, String category, String lang, final SQLOperationCallback callback){
        feedsRESTInterface.addFeed(title, url, category, lang).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }


    /*********************** User - Feeds *********************************/

    /**
     * Gets the list of all the User's Feeds
     * @param callback Callback for API response management
     */
    public void getUserFeeds(String userEmail, final FeedCallback callback){
        final List<Feed> feeds = new ArrayList<>();

        userRESTInterface.getUserFeeds(userEmail)
                .enqueue(new retrofit2.Callback<List<Feed>>() {
                    @Override
                    public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                        for (Feed feed: response.body()) {
                            feeds.add(feed);
                        }
                        callback.onLoad(feeds);
                    }

                    @Override
                    public void onFailure(Call<List<Feed>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Gets the list of all the User's FeedGroups
     * @param callback Callback for API response management
     */
    public void getUserFeedGroups(int userId, final FeedGroupCallback callback){
        final List<FeedGrouping> feedGroups = new ArrayList<>();

        userRESTInterface.getUserFeedGroups(userId)
                .enqueue(new retrofit2.Callback<List<FeedGrouping>>() {
                    @Override
                    public void onResponse(Call<List<FeedGrouping>> call, Response<List<FeedGrouping>> response) {
                        for (FeedGrouping feedGroup: response.body()) {
                            feedGroups.add(feedGroup);
                        }
                        callback.onLoad(feedGroups);
                    }

                    @Override
                    public void onFailure(Call<List<FeedGrouping>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Gets the article Checkpoint of the User's Feed-Multifeed Group
     * @param feed
     * @param multifeed
     * @param callback Callback for API response management
     */
    public void getUserFeedCheckpoint(int feed, int multifeed, final ArticleCallback callback){
        final List<Article> articles = new ArrayList<>();

        userRESTInterface.getUserFeedCheckpoint(feed, multifeed)
                .enqueue(new retrofit2.Callback<List<Article>>() {
                    @Override
                    public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                        for (Article article: response.body()) {
                            articles.add(article);
                        }
                        callback.onLoad(articles);
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Add a Feed to a User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback Callback for API response management
     */
    public void addUserFeed(int feed, int multifeed, final SQLOperationCallback callback){
        userRESTInterface.addUserFeed(feed, multifeed).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Add an article checkpoint to the Feed associated with the User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param article Article hashId, representing the check point of the feed associated with a user and multifeed
     * @param callback Callback for API response management
     */
    public void addUserFeedCheckpoint(int feed, int multifeed, long article, final SQLOperationCallback callback){
        userRESTInterface.addUserFeedCheckpoint(feed, multifeed, article).enqueue(new retrofit2.Callback<SQLOperation>() {
            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Delete a Feed related to a User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback Callback for API response management
     */
    public void deleteUserFeed(int feed, int multifeed, final SQLOperationCallback callback){
        userRESTInterface.deleteUserFeed(feed, multifeed).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /*********************** User - Multifeeds *********************************/

    /**
     * Gets the list of all the User's Multifeeds
     * @param userEmail Collection's owner email
     * @param callback Callback for API response management
     */
    public void getUserMultifeeds(String userEmail, final MultifeedCallback callback){
        final List<Multifeed> multifeeds = new ArrayList<>();

        userRESTInterface.getUserMultifeeds(userEmail)
                .enqueue(new retrofit2.Callback<List<Multifeed>>() {
                    @Override
                    public void onResponse(Call<List<Multifeed>> call, Response<List<Multifeed>> response) {
                        for (Multifeed multifeed: response.body()) {
                            multifeeds.add(multifeed);
                        }
                        callback.onLoad(multifeeds);
                    }

                    @Override
                    public void onFailure(Call<List<Multifeed>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Add a Multifeed to a User
     * @param title Title of the Multifeed
     * @param user Owner of the multifeed
     * @param color Color used to indicate the multifeed on user's app
     * @param callback Callback for API response management
     */
    public void addUserMultifeed(String title, int user, int color, int rating, final SQLOperationCallback callback){
        userRESTInterface.addUserMultifeed(title, user, color, rating).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Delete a Multifeed related to a User
     * @param id Multifeed's identification number
     * @param callback Callback for API response management
     */
    public void deleteUserMultifeed(int id, final SQLOperationCallback callback){
        userRESTInterface.deleteUserMultifeed(id).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Update a Multifeed with a certain id
     * @param id        Multifeed's Id
     * @param newTitle  The new title to set instead of the old one
     * @param newColor  The new color to set instead of the old one
     */
    public void updateUserMultifeed(int id, String newTitle, int newColor, int newRating, final SQLOperationCallback callback){
        userRESTInterface.updateUserMultifeed(id, newTitle, newColor, newRating).enqueue(new retrofit2.Callback<SQLOperation>() {
            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /*********************** User - Collections *********************************/

    /**
     * Gets the list of all the User's Collections
     * @param userEmail Colelction's owner email
     * @param callback Callback for API response management
     */
    public void getUserCollections(String userEmail, final CollectionCallback callback){
        final List<Collection> collections = new ArrayList<>();

        userRESTInterface.getUserCollections(userEmail)
                .enqueue(new retrofit2.Callback<List<Collection>>() {
                    @Override
                    public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
                        for (Collection collection: response.body()) {
                            collections.add(collection);
                        }
                        callback.onLoad(collections);
                    }

                    @Override
                    public void onFailure(Call<List<Collection>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Add a Collection to a User
     * @param title Title of the Collection
     * @param user Owner of the Collection
     * @param color Color used to indicate the Collection on user's app
     * @param callback Callback for API response management
     */
    public void addUserCollection(String title, int user, int color, final SQLOperationCallback callback){
        userRESTInterface.addUserCollection(title, user, color).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Delete a Collection related to a User, due to table relations, this api also needs to delete all
     * the saved_articles associated to this collection(this is the first thing done).
     * @param id Collection's identification number
     * @param callback SQLOperationListCallback for API response management:
     *                 - List's first SQLOperation indicates the rows deleted from saved_article table;
     *                 - List's second SQLOperation indicates the rows deleted from the collection table;
     */
    public void deleteUserCollection(int id, final SQLOperationListCallback callback){
        final List<SQLOperation> sqlOperationList = new ArrayList<>();

        userRESTInterface.deleteUserCollection(id).enqueue(new retrofit2.Callback<List<SQLOperation>>() {
            @Override
            public void onResponse(Call<List<SQLOperation>> call, Response<List<SQLOperation>> response) {
                for (SQLOperation sqlOperation :response.body()){
                    sqlOperationList.add(sqlOperation);
                }
                callback.onLoad(sqlOperationList);
            }

            @Override
            public void onFailure(Call<List<SQLOperation>> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Update a Collection with a certain id
     * @param id        Collection's Id
     * @param newTitle  The new title to set instead of the old one
     * @param newColor  The new color to set instead of the old one
     */
    public void updateUserCollection(int id, String newTitle, int newColor, final SQLOperationCallback callback){
        userRESTInterface.updateUserCollection(id, newTitle, newColor).enqueue(new retrofit2.Callback<SQLOperation>() {
            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }


    /*********************** User - Articles *********************************/

    /**
     * Gets the list of all the User's Articles by feed
     * @param feed
     * @param callback Callback for API response management
     */
    public void getUserArticlesByFeed(int feed, final ArticleCallback callback){
        final List<Article> articles = new ArrayList<>();

        userRESTInterface.getUserArticlesByFeed(feed)
                .enqueue(new retrofit2.Callback<List<Article>>() {
                    @Override
                    public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                        for (Article article: response.body()) {
                            articles.add(article);
                        }
                        callback.onLoad(articles);
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Gets the list of all the User's Articles by user id
     * @param userId
     * @param callback Callback for API response management
     */
    public void getUserArticles(int userId, final ArticleCallback callback){
        final List<Article> articles = new ArrayList<>();

        userRESTInterface.getUserArticles(userId)
                .enqueue(new retrofit2.Callback<List<Article>>() {
                    @Override
                    public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                        for (Article article: response.body()) {
                            articles.add(article);
                        }
                        callback.onLoad(articles);
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
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
    public void addUserArticle(String title, String description, String comment, String link, String img_link, String pub_date, int user, int feed,
                               final SQLOperationCallback callback){
        userRESTInterface.addUserArticle(title, description, comment, link, img_link, pub_date, user, feed).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Delete a Article related to a User
     * @param hashId
     * @param callback Callback for API response management
     */
    public void deleteUserArticle(long hashId, final SQLOperationCallback callback){
        userRESTInterface.deleteUserArticle(hashId).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }


    /*********************** User - SavedArticles *********************************/
    /**
     * Gets the list of all the User's Articles saved in Collection by collectionId
     * @param collectionId Collection's Id
     * @param callback Callback for API response management
     */
    public void getUserArticlesSavedInCollection(int collectionId, final ArticleCallback callback){
        final List<Article> articles = new ArrayList<>();

        userRESTInterface.getUserArticlesSavedInCollection(collectionId)
                .enqueue(new retrofit2.Callback<List<Article>>() {
                    @Override
                    public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                        for (Article article: response.body()) {
                            articles.add(article);
                        }
                        callback.onLoad(articles);
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }


    /**
     * Gets the list of all the User's SavedArticles saved the user with the specified userId
     * @param userId    User's Id
     * @param callback Callback for API response management
     */
    public void getUserSavedArticles(int userId, final SavedArticleCallback callback){
        final List<SavedArticle> savedArticles = new ArrayList<>();

        userRESTInterface.getUserSavedArticles(userId)
                .enqueue(new retrofit2.Callback<List<SavedArticle>>() {
                    @Override
                    public void onResponse(Call<List<SavedArticle>> call, Response<List<SavedArticle>> response) {
                        for (SavedArticle savedArticle: response.body()) {
                            savedArticles.add(savedArticle);
                        }
                        callback.onLoad(savedArticles);
                    }

                    @Override
                    public void onFailure(Call<List<SavedArticle>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Add an SavedArticle to the User
     * @param article
     * @param collection
     * @param callback Callback for API response management
     */
    public void addUserSavedArticle(long article, int collection, final SQLOperationCallback callback){
        userRESTInterface.addUserSavedArticle(article, collection).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Add an Article and a SavedArticle(Association with a collection). The OnResponse will return with
     * two SQLperation results stored in a list (the first one is related to the Article inserption, while
     * the second SQLOperation refers to the SavedArticle insertion). Article insertion does not fail on duplicate
     * hash_id (if already present on the DB, then only the Saved article will be inserted)
     * @param title
     * @param description
     * @param comment
     * @param link
     * @param img_link
     * @param pub_date
     * @param userId
     * @param feedId
     * @param collectionId
     * @param callback      SQLOperationListCallback callback interface
     */
    public void addUserArticleAssociatedToCollection(String title, String description, String comment, String link, String img_link,
                                                     String pub_date, int userId, int feedId, int collectionId, final SQLOperationCallback callback){
        userRESTInterface.addUserArticleAssociatedToCollection(title, description, comment, link, img_link, pub_date, userId, feedId, collectionId)
                .enqueue(new retrofit2.Callback<SQLOperation>() {
                    @Override
                    public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                        callback.onLoad(response.body());
                    }

                    @Override
                    public void onFailure(Call<SQLOperation> call, Throwable t) {
                        callback.onFailure();
                    }
        });
    }

    /**
     * Delete a SavedArticle related to a User
     * @param article
     * @param collection
     * @param callback Callback for API response management
     */
    public void deleteUserSavedArticle(long article, int collection, final SQLOperationCallback callback){
        userRESTInterface.deleteUserSavedArticle(article, collection).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }


    /*********************** User - ReadArticles *********************************/
    /**
     * Gets the list of all the User's ReadArticles
     * @param user
     * @param callback Callback for API response management
     */
    public void getUserReadArticles(int user, final ReadArticleCallback callback){
        final List<ReadArticle> readArticles = new ArrayList<>();

        userRESTInterface.getUserReadArticles(user)
                .enqueue(new retrofit2.Callback<List<ReadArticle>>() {
                    @Override
                    public void onResponse(Call<List<ReadArticle>> call, Response<List<ReadArticle>> response) {
                        for (ReadArticle readArticle : response.body()) {
                            readArticles.add(readArticle);
                        }
                        callback.onLoad(readArticles);
                    }

                    @Override
                    public void onFailure(Call<List<ReadArticle>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Add an Article's Opened to the User
     * @param user
     * @param article
     * @param callback Callback for API response management
     */
    public void addUserOpenedArticle(int user, long article, final SQLOperationCallback callback){
        userRESTInterface.addUserOpenedArticle(user, article).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Add an Article's Read to the User
     * @param user
     * @param article
     * @param callback Callback for API response management
     */
    public void addUserReadArticle(int user, long article, final SQLOperationCallback callback){
        userRESTInterface.addUserReadArticle(user, article).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Add an Article's Feedback to the User
     * @param user
     * @param article
     * @param vote
     * @param callback Callback for API response management
     */
    public void addUserFeedbackArticle(int user, long article, int vote, final SQLOperationCallback callback){
        userRESTInterface.addUserFeedbackArticle(user, article, vote).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /**
     * Delete a ReadArticle related to a User
     * @param user
     * @param article
     * @param callback Callback for API response management
     */
    public void deleteUserReadArticle(int user, long article, final SQLOperationCallback callback){
        userRESTInterface.deleteUserReadArticle(user, article).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                SQLOperation sqlOperation = response.body();
                callback.onLoad(sqlOperation);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    /*********************** Articles *********************************/
    /**
     * Gets the score for an article
     * @param articleHashId
     * @param callback Callback for API response management
     */
    public void getArticleScore(long articleHashId, final JsonArrayCallback callback) {
        articlesRESTInterface.getArticleScore(articleHashId).enqueue(new retrofit2.Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                callback.onLoad(response.body().getAsJsonObject());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onFailure();
            }

        });
    }

    /**
     * Gets the scores for a list of articles
     * @param articlesHashes
     * @param callback Callback for API response management
     */
    public void getArticlesScores(List<String> articlesHashes, final ArticlesScoresCallback callback) {
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("ids", articlesHashes);

        articlesRESTInterface.getArticlesScores(hashMap).enqueue(new retrofit2.Callback<List<ArticlesScores>>() {

            @Override
            public void onResponse(Call<List<ArticlesScores>> call, Response<List<ArticlesScores>> response) {
                callback.onLoad(response.body());
            }

            @Override
            public void onFailure(Call<List<ArticlesScores>> call, Throwable t) {
                callback.onFailure();
            }


        });
    }
}
