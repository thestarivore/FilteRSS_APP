package com.company.rss.rss.restful_api.interfaces;

import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.SQLOperation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FeedsRESTInterface {
    @GET("/v1/feeds")
    public Call<List<Feed>> getAllFeeds();

    @GET("/v1/feeds")
    public Call<List<Feed>> getFilteredFeedsBySearch(@Query("search") String searchFilter);

    @GET("/v1/feeds")
    public Call<List<Feed>> getFilteredFeedsByCategory(@Query("category") String category);

    @GET("/v1/feeds")
    public Call<List<Feed>> getFilteredFeedsBySearchAndCategory(@Query("search") String searchFilter,
                                                                @Query("category") String category);

    @FormUrlEncoded
    @POST("/v1/feeds")
    public Call<SQLOperation> addFeed(@Field("title") String title,
                                      @Field("url") String url,
                                      @Field("category") String category,
                                      @Field("lang") String lang);
}
