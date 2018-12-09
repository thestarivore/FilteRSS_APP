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

public class Multifeed implements Serializable {
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

    //TODO: aggiunto perche usato nella mockapp, ma non c'era nel ER_Model originale, valutare se gestire la lista dei feeds qui, oppure esternamente
    private List<Feed> feeds;
    //TODO: aggiunto perche usato nella mockapp, ma non c'era nel ER_Model originale, valutare se serve
    private int importance;

    public Multifeed() {
    }

    public Multifeed(int id, String title, int user, int color) {
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

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public int getFeedCount() {
        return feeds.size();
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    @Override
    public String toString() {
        return "Multifeed{" +
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

        Multifeed multifeed = (Multifeed) o;

        if (id != multifeed.id) return false;
        if (user != multifeed.user) return false;
        if (color != multifeed.color) return false;
        return title.equals(multifeed.title);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + user;
        result = 31 * result + color;
        return result;
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
            multifeedsStrings[i] = multifeeds.get(i).getTitle();
        }
        return multifeedsStrings;
    }
}
