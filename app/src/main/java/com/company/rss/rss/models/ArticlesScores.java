package com.company.rss.rss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArticlesScores implements Serializable {
    @SerializedName("article")
    @Expose
    private String article;

    @SerializedName("score")
    @Expose
    private String score;


    public ArticlesScores() {
    }

    public ArticlesScores(String article, String score) {
        this.article = article;
        this.score = score;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ArticlesScores{" +
                "article='" + article + '\'' +
                ", score=" + score +
                '}';
    }
}
