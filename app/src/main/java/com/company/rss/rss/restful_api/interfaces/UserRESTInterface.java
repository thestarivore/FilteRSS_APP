package com.company.rss.rss.restful_api.interfaces;



import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.Collection;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.FeedGrouping;
import com.company.rss.rss.models.Multifeed;
import com.company.rss.rss.models.ReadArticle;
import com.company.rss.rss.models.SQLOperation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserRESTInterface {
    /*********************** User - Feeds *********************************/
    @GET("/v1/user/feeds")
    public Call<List<Feed>> getUserFeeds(@Query("email") String userEmail);

    @GET("/v1/user/feeds/group")
    public Call<List<FeedGrouping>> getUserFeedGroups(@Query("userId") int userId);

    @GET("/v1/user/feeds/checkpoint")
    public Call<List<Article>> getUserFeedCheckpoint(@Query("feed") int feed,
                                                     @Query("multifeed") int multifeed);

    @FormUrlEncoded
    @PUT("/v1/user/feeds")
    public Call<SQLOperation> addUserFeed(@Field("feed") int feed,
                                          @Field("multifeed") int multifeed);

    @FormUrlEncoded
    @PATCH("/v1/user/feeds/checkpoint")
    public Call<SQLOperation> addUserFeedCheckpoint(@Field("feed") int feed,
                                                    @Field("multifeed") int multifeed,
                                                    @Field("article") long article);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/v1/user/feeds", hasBody = true)
    public Call<SQLOperation> deleteUserFeed(@Field("feed") int feed,
                                             @Field("multifeed") int multifeed);


    /*********************** User - Multifeeds *********************************/
    @GET("/v1/user/multifeeds")
    public Call<List<Multifeed>> getUserMultifeeds(@Query("email") String userEmail);

    @FormUrlEncoded
    @PUT("/v1/user/multifeeds")
    public Call<SQLOperation> addUserMultifeed(@Field("title") String title,
                                               @Field("user") int user,
                                               @Field("color") int color);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/v1/user/multifeeds", hasBody = true)
    public Call<SQLOperation> deleteUserMultifeed(@Field("id") int id);


    /*********************** User - Collections *********************************/
    @GET("/v1/user/collections")
    public Call<List<Collection>> getUserCollections(@Query("email") String userEmail);

    @FormUrlEncoded
    @PUT("/v1/user/collections")
    public Call<SQLOperation> addUserCollection(@Field("title") String title,
                                                @Field("user") int user,
                                                @Field("color") int color);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/v1/user/collections", hasBody = true)
    public Call<SQLOperation> deleteUserCollection(@Field("id") int id);


    /*********************** User - Articles *********************************/
    @GET("/v1/user/articles")
    public Call<List<Article>> getUserArticles(@Query("feed") int feed);

    @FormUrlEncoded
    @PUT("/v1/user/articles")
    public Call<SQLOperation> addUserArticle(@Field("title") String title,
                                             @Field("description") String description,
                                             @Field("comment") String comment,
                                             @Field("link") String link,
                                             @Field("img_link") String img_link,
                                             @Field("pub_date") String pub_date,
                                             @Field("user") int user,
                                             @Field("feed") int feed);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/v1/user/articles", hasBody = true)
    public Call<SQLOperation> deleteUserArticle(@Field("hash_id") long hashId);


    /*********************** User - SavedArticles *********************************/
    @GET("/v1/user/articles/saved")
    public Call<List<Article>> getUserSavedArticles(@Query("id") int id);

    @FormUrlEncoded
    @PUT("/v1/user/articles/saved")
    public Call<SQLOperation> addUserSavedArticle(@Field("article") long article,
                                                  @Field("collection") int collection);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/v1/user/articles/saved", hasBody = true)
    public Call<SQLOperation> deleteUserSavedArticle(@Field("article") long article,
                                                     @Field("collection") int collection);


    /*********************** User - ReadArticles *********************************/
    @GET("/v1/user/articles/read")
    public Call<List<ReadArticle>> getUserReadArticles(@Query("user") int user);

    @FormUrlEncoded
    @PUT("/v1/user/articles/read")
    public Call<SQLOperation> addUserReadArticle(@Field("user") int user,
                                                 @Field("article") long article,
                                                 @Field("vote") int vote);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/v1/user/articles/read", hasBody = true)
    public Call<SQLOperation> deleteUserReadArticle(@Field("user") int user,
                                                    @Field("article") long article);
}