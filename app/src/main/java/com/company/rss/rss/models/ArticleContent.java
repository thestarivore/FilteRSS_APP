package com.company.rss.rss.models;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ArticleContent {


    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public static final List<Article> ARTICLES = new ArrayList<Article>();


    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();
    public static final Map<String, Article> ARTICLES_MAP = new HashMap<String, Article>();


    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addArticle(createMockArticle(i));
        }
    }

    private static void addArticle(Article article) {
        ARTICLES.add(article);
        ARTICLES_MAP.put(article.id, article);
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static Article createMockArticle(int i) {
        return new Article(String.valueOf(i), makeTitle(), makeBody(), makeSub());
    }

    private static String makeSub() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getCountry() + " / " + lorem.getCity() + " / " + lorem.getName();
    }

    private static String makeBody() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getParagraphs(2, 40);
    }

    private static String makeTitle() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getTitle(1, 18);
    }


    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    /**
     * The article model
     */
    public static class Article implements Serializable {
        public final String id;
        public final String title;
        public final String excerpt;
        public final String body;
        public final String sub;

        public Article(String id, String title, String body, String sub) {
            this.id = id;
            this.title = title;
            this.excerpt = body; // TODO: extract excerpt
            this.body = body;
            this.sub = sub;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }

        public String getSub() {
            return sub;
        }

        public int getReadingTime() {
            if (this.body == null || this.body.isEmpty()) {
                return 0;
            }

            StringTokenizer tokens = new StringTokenizer(this.body);
            return tokens.countTokens() / 130; // 130 is the avg words per minute
        }
    }
}
