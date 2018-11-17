package com.company.rss.rss.models;

import android.support.annotation.NonNull;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public class Article implements Serializable {
    public final int id;
    public final String title;
    public final String excerpt;
    public final String body;
    public final String source;
    public final Date pubblishTime;

    public Article(int id, String title, String body, String source, Date pubblishTime) {
        this.id = id;
        this.title = title;
        this.excerpt = body; // TODO: extract excerpt
        this.body = body;
        this.source = source;
        this.pubblishTime = pubblishTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getSource() {
        return source;
    }

    public int getReadingTime() {
        if (this.body == null || this.body.isEmpty()) {
            return 0;
        }

        StringTokenizer tokens = new StringTokenizer(this.body);
        return tokens.countTokens() / 130; // 130 is the avg words read per minute
    }

    @NonNull
    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    private static Article createMockArticle() {
        Random random = new Random();
        return new Article(random.nextInt(), makeTitle(), makeBody(), makeSource(), new Date());
    }

    private static String makeSource() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getName();
    }

    private static String makeBody() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getParagraphs(2, 40);
    }

    private static String makeTitle() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getTitle(1, 18);
    }

    public static List<Article> generateMockupArticle(int length) {
        List<Article> articles = new ArrayList<Article>(length);
        for (int i = 0; i < length; i++) {
            articles.add(createMockArticle());
        }
        return articles;
    }
}
