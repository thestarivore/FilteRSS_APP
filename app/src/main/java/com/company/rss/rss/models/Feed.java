package com.company.rss.rss.models;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.ArrayList;
import java.util.Random;

public class Feed {
    private final int id;
    private final String category;
    private final String name;

    public Feed(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public static Feed[] generateMockupFeeds(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        Feed[] feeds = new Feed[length];
        for (int i = 0; i < length; i++) {
            feeds[i] = new Feed(random.nextInt(), lorem.getName(), lorem.getCity());
        }
        return feeds;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
