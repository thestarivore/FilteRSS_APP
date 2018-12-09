package com.company.rss.rss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Article implements Serializable {
    @SerializedName("hash_id")
    @Expose
    private long hashId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("img_link")
    @Expose
    private String imgLink;

    @SerializedName("pub_date")
    @Expose
    private Date pubDate;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("feed")
    @Expose
    private String feed;

    //TODO: aggiunto perche usato nella mockapp, ma non c'era nel ER_Model originale, valutare se serve
    private String thumbnail;
    //TODO: aggiunto perche usato nella mockapp, ma non c'era nel ER_Model originale, valutare se serve
    private String excerpt;

    public Article() {
    }

    public Article(long hashId, String title, String description, String link, Date pubDate, String imgLink, String thumbnail) {
        this.hashId = hashId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.imgLink = imgLink;
        this.pubDate = pubDate;
        this.thumbnail = thumbnail;
    }

    public Article(long hashId, String title, String description, String comment, String link, String imgLink, Date pubDate, String user, String feed, String thumbnail) {
        this.hashId = hashId;
        this.title = title;
        this.description = description;
        this.comment = comment;
        this.link = link;
        this.imgLink = imgLink;
        this.pubDate = pubDate;
        this.user = user;
        this.feed = feed;
        this.thumbnail = thumbnail;
    }

    public long getHashId() {
        return hashId;
    }

    public void setHashId(long hashId) {
        this.hashId = hashId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    @Override
    public String toString() {
        return "Article{" +
                "hashId=" + hashId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", link='" + link + '\'' +
                ", imgLink='" + imgLink + '\'' +
                ", pubDate=" + pubDate +
                ", user='" + user + '\'' +
                ", feed='" + feed + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (hashId != article.hashId) return false;
        if (!title.equals(article.title)) return false;
        if (description != null ? !description.equals(article.description) : article.description != null)
            return false;
        if (comment != null ? !comment.equals(article.comment) : article.comment != null)
            return false;
        if (!link.equals(article.link)) return false;
        if (imgLink != null ? !imgLink.equals(article.imgLink) : article.imgLink != null)
            return false;
        if (!pubDate.equals(article.pubDate)) return false;
        if (!user.equals(article.user)) return false;
        return feed.equals(article.feed);
    }

    @Override
    public int hashCode() {
        int result = (int) (hashId ^ (hashId >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + link.hashCode();
        result = 31 * result + (imgLink != null ? imgLink.hashCode() : 0);
        result = 31 * result + pubDate.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + feed.hashCode();
        return result;
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

    public int getReadingTime() {
        if (this.description == null || this.description.isEmpty()) {
            return 0;
        }

        StringTokenizer tokens = new StringTokenizer(this.description);
        return tokens.countTokens() / 130; // 130 is the avg words read per minute
    }

    public static List<Article> generateMockupArticles(int length) {
        List<Article> articles = new ArrayList<Article>(length);
        for (int i = 0; i < length; i++) {
            articles.add(createMockArticle());
        }
        return articles;
    }
}

