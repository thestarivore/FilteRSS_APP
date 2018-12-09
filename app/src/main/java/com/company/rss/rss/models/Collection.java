package com.company.rss.rss.models;

import android.graphics.Color;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Collection implements Serializable{
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("user")
    @Expose
    private int user;

    @SerializedName("color")
    @Expose
    private int color;

    //TODO: aggiunto perche usato nella mockapp, ma non c'era nel ER_Model originale, valutare se gestire la lista degli articoli qui, oppure esternamente
    private List<Article> articles;

    public Collection() {
    }

    public Collection(int id, String title, int color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    public Collection(int id, String title, int user, int color) {
        this.id = id;
        this.title = title;
        this.user = user;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", user=" + user +
                ", color=" + color +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;

        if (id != that.id) return false;
        if (user != that.user) return false;
        if (color != that.color) return false;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + user;
        result = 31 * result + color;
        return result;
    }

    public static List<Collection> generateMockupCollections(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        List<Collection> collections = new ArrayList<Collection>(length);
        for (int i = 0; i < length; i++) {
            collections.add(new Collection(
                    random.nextInt(),
                    lorem.getName(),
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))));
            collections.get(i).setArticles(Article.generateMockupArticles(10));
        }
        return collections;
    }
}
