package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.ArticlesScores;

import java.util.List;

public interface ArticlesScoresCallback {
    public void onLoad(List<ArticlesScores> articlesScores);
    public void onFailure();
}
