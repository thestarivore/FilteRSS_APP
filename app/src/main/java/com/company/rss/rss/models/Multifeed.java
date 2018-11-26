package com.company.rss.rss.models;

import android.graphics.Color;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Multifeed implements Serializable {
    private int id;
    private String name;
    private int color;
    private int feedCount;
    private int importance;
    private List<Feed> feeds;


    public Multifeed(int id, String name, int color, int importance, List<Feed> feeds) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.feedCount = 0;
        this.importance = importance;
        this.feeds = feeds;
    }

    public int getColor() {
        return color;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(int feedCount) {
        this.feedCount = feedCount;
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

    public static Multifeed[] generateMockupMultifeeds(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        Multifeed[] multifeeds = new Multifeed[length];
        for (int i = 0; i < length; i++) {
            multifeeds[i] = new Multifeed(
                    random.nextInt(),
                    lorem.getName(),
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)),
                    random.nextInt(5),
                    Feed.generateMockupFeeds(random.nextInt(10))
            );
            multifeeds[i].setFeedCount(random.nextInt(10));
        }
        return multifeeds;
    }

    public static String[] toStrings(Multifeed[] multifeeds) {
        String[] multifeedsStrings = new String[multifeeds.length];
        for (int i = 0; i < multifeeds.length; i++) {
            multifeedsStrings[i] = multifeeds[i].getName();
        }
        return multifeedsStrings;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    @Override
    public String toString() {
        return "Multifeed{" +
                "id=" + id +
                ", title='" + name + '\'' +
                '}';
    }

}
