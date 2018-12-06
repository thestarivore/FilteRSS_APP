package com.company.rss.rss.models;

import android.graphics.Color;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Collection {
    private final int id;
    private int color;
    private String name;
    private List<Article> articles;

    public Collection(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", color=" + color +
                ", name='" + name + '\'' +
                '}';
    }
}
