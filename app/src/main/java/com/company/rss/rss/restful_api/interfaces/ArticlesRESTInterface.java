package com.company.rss.rss.restful_api.interfaces;

import com.company.rss.rss.models.ArticlesScores;
import com.company.rss.rss.models.SQLOperation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ArticlesRESTInterface {

    @GET("/v1/articles/score/{articleHashId}")
    Call<JsonObject> getArticleScore(@Path("articleHashId") long articleHashId);

    @POST("/v1/articles/scores")
    Call<List<ArticlesScores>> getArticlesScores(@Body HashMap<String, Object> body);
}