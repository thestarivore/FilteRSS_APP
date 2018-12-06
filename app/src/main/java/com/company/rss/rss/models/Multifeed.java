package com.company.rss.rss.models;

import android.graphics.Color;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Multifeed implements Serializable {
    private int id;
    private String name;
    private int color;
    private int importance;
    private List<Feed> feeds;


    public Multifeed(int id, String name, int color, int importance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.importance = importance;
    }

    public int getColor() {
        return color;
    }

    public int getFeedCount() {
        return feeds.size();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImportance() {
        return importance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public static List<Multifeed> generateMockupMultifeeds(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        List<Multifeed> multifeeds = new ArrayList<Multifeed>(length);
        for (int i = 0; i < length; i++) {
            multifeeds.add(new Multifeed(
                    random.nextInt(),
                    lorem.getName(),
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)),
                    random.nextInt(5)));
            multifeeds.get(i).setFeeds(Feed.generateMockupFeeds(random.nextInt(10)));
        }
        return multifeeds;
    }

    public static String[] toStrings(List<Multifeed> multifeeds) {
        String[] multifeedsStrings = new String[multifeeds.size()];
        for (int i = 0; i < multifeeds.size(); i++) {
            multifeedsStrings[i] = multifeeds.get(i).getName();
        }
        return multifeedsStrings;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) { this.feeds = feeds; }

    @Override
    public String toString() {
        return "Multifeed{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", importance=" + importance +
                '}';
    }
}
