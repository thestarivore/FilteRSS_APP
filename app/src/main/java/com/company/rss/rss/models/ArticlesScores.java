package com.company.rss.rss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArticlesScores implements Serializable {
    @SerializedName("article")
    @Expose
    private Long article;

    @SerializedName("score")
    @Expose
    private Float score;


    public ArticlesScores() {
    }

    public ArticlesScores(Long article, Float score) {
        this.article = article;
        this.score = score;
    }

    public Long getArticle() {
        return article;
    }

    public void setArticle(Long article) {
        this.article = article;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
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
