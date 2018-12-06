package com.company.rss.rss.models;

import android.support.annotation.NonNull;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Article implements Serializable {
    private final int id;
    private final String title;
    private final String excerpt;
    private final String body;
    private final String source;
    private final Date pubblishTime;
    private final String image;
    private final String thumbnail;

    public Article(int id, String title, String body, String source, Date pubblishTime, String image, String thumbnail) {
        this.id = id;
        this.title = title;
        this.excerpt = body; // TODO: extract excerpt
        this.body = body;
        this.source = source;
        this.pubblishTime = pubblishTime;
        this.image = image;
        this.thumbnail = thumbnail;
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
        int imageId = random.nextInt(1085);
        return new Article(random.nextInt(), makeTitle(), makeBody(), makeSource(), new Date(), makeImage(imageId), makeThumbnail(imageId));
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

    private static String makeThumbnail(int imageId){
        Random random = new Random();
        return "https://picsum.photos/200/300?image=" + imageId;
    }

    private static String makeImage(int imageId){
        Random random = new Random();
        return "https://picsum.photos/2000/3000?image=" + imageId;
    }

    public String getImage() {
        return image;
    }

    public String getThumbnail() { return thumbnail; }

    public String getExcerpt() {
        return excerpt;
    }

    public Date getPubblishTime() {
        return pubblishTime;
    }

    public static List<Article> generateMockupArticles(int length) {
        List<Article> articles = new ArrayList<Article>(length);
        for (int i = 0; i < length; i++) {
            articles.add(createMockArticle());
        }
        return articles;
    }
}
