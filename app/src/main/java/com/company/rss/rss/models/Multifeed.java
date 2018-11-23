package com.company.rss.rss.models;

import android.graphics.Color;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.Map;
import java.util.Random;

public class Multifeed {
    private int id;
    private String name;
    private int color;
    private int feedCount;


    public Multifeed(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.feedCount = 0;
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

    public static Multifeed[] generateMockupMultifeeds(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        Multifeed[] multifeeds = new Multifeed[length];
        for (int i = 0; i < length; i++) {
            multifeeds[i] = new Multifeed(random.nextInt(), lorem.getName(), Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
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

    @Override
    public String toString() {
        return "Multifeed{" +
                "id=" + id +
                ", title='" + name + '\'' +
                '}';
    }
}
