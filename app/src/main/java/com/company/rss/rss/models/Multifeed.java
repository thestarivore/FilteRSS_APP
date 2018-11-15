package com.company.rss.rss.models;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.Map;
import java.util.Random;

public class Multifeed {
    private int id;
    private String title;


    public Multifeed(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static Multifeed[] generateMockupMultifeeds(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        Multifeed[] multifeeds = new Multifeed[length];
        for (int i = 0; i < length; i++) {
            multifeeds[i] = new Multifeed(random.nextInt(), lorem.getName());
        }
        return multifeeds;
    }

    public static String[] toStrings(Multifeed[] multifeeds) {
        String[] multifeedsStrings = new String[multifeeds.length];
        for (int i = 0; i < multifeeds.length; i++) {
            multifeedsStrings[i] = multifeeds[i].getTitle();
        }
        return multifeedsStrings;
    }

    @Override
    public String toString() {
        return "Multifeed{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
