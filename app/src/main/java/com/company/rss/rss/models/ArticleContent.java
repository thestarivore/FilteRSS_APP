package com.company.rss.rss.models;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ArticleContent {

    public static final List<Article> ARTICLES = new ArrayList<Article>();
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

    public static Article createMockArticle(int i) {
        return new Article(String.valueOf(i), makeTitle(), makeBody(), makeSource(), new Date());
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

    public static class Article implements Serializable {
        public final String id;
        public final String title;
        public final String excerpt;
        public final String body;
        public final String source;
        public final Date pubblishTime;

        public Article(String id, String title, String body, String source, Date pubblishTime) {
            this.id = id;
            this.title = title;
            this.excerpt = body; // TODO: extract excerpt
            this.body = body;
            this.source = source;
            this.pubblishTime = pubblishTime;
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
    }
}
