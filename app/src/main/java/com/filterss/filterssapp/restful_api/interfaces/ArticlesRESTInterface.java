package com.filterss.filterssapp.restful_api.interfaces;

import com.filterss.filterssapp.models.ArticlesScores;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ArticlesRESTInterface {

    @GET("/v1/articles/score/{articleHashId}")
    Call<JsonObject> getArticleScore(@Path("articleHashId") long articleHashId);

    @POST("/v1/articles/scores")
    Call<List<ArticlesScores>> getArticlesScores(@Body HashMap<String, Object> body);
}