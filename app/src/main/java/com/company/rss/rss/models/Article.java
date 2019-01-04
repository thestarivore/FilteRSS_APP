package com.company.rss.rss.models;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import net.boeckling.crc.CRC64;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private int user;

    @SerializedName("feed")
    @Expose
    private int feed;

    private String feedName;
    private String iconURL;
    private int color;
    private float score;

    //TODO: aggiunto perche usato nella mockapp, ma non c'era nel ER_Model originale, valutare se serve
    private String excerpt;
    private String author;      //TODO: Valutare se aggiungere sul DB

    public Article() {
    }

    public Article(long hashId, String title, String description, String link, Date pubDate, String imgLink) {
        this.hashId = hashId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.imgLink = imgLink;
        this.pubDate = pubDate;
    }

    public Article(long hashId, String title, String description, String comment, String link, String imgLink, Date pubDate, int user, int feed) {
        this.hashId = hashId;
        this.title = title;
        this.description = description;
        this.comment = comment;
        this.link = link;
        this.imgLink = imgLink;
        this.pubDate = pubDate;
        this.user = user;
        this.feed = feed;
    }

    public long getHashId() {
        return hashId;
    }

    public void setHashId(long hashId) {
        this.hashId = hashId;
    }

    /**
     * Compute the HashID locally using the same Hashing function used on the server(CRC64 ECMA182).
     * The string being hashed is the Article's URL.
     * @return HashId newly generated
     */
    public long computeHashId(){
        String str = this.link;
        CRC64 crc64 = new CRC64(str.getBytes(), str.length());
        this.hashId = crc64.getValue() - 9223372036854775807L;
        return this.hashId;
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

    /**
     * Return the publication date in String
     * @return String containig the publication date
     */
    public String getPubDateString(String pattern) {
        if (pubDate != null) {
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            String strDate = dateFormat.format(pubDate);
            return strDate;
        }
        else
            return "";
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getFeed() {
        return feed;
    }

    public void setFeed(int feed) {
        this.feed = feed;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedIcon(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getFeedIcon() {
        return iconURL;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Article{" +
                "score=" + score +
                ", hashId=" + hashId + '\'' +
                ", title='" + title + '\'' +
                //", description='" + description + '\'' +
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
        if (user != article.user) return false;
        if (feed != article.feed) return false;
        if (!title.equals(article.title)) return false;
        if (description != null ? !description.equals(article.description) : article.description != null)
            return false;
        if (comment != null ? !comment.equals(article.comment) : article.comment != null)
            return false;
        if (link != null ? !link.equals(article.link) : article.link != null) return false;
        if (imgLink != null ? !imgLink.equals(article.imgLink) : article.imgLink != null)
            return false;
        if (pubDate != null ? !pubDate.equals(article.pubDate) : article.pubDate != null)
            return false;
        if (excerpt != null ? !excerpt.equals(article.excerpt) : article.excerpt != null)
            return false;
        return author != null ? author.equals(article.author) : article.author == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (hashId ^ (hashId >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (imgLink != null ? imgLink.hashCode() : 0);
        result = 31 * result + (pubDate != null ? pubDate.hashCode() : 0);
        result = 31 * result + user;
        result = 31 * result + feed;
        result = 31 * result + (excerpt != null ? excerpt.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        return result;
    }

    private static Article createMockArticle() {
        Random random = new Random();
        int imageId = random.nextInt(1085);
        return new Article(random.nextInt(), makeTitle(), makeBody(), makeSource(), new Date(), makeImage(imageId));
    }

    private static String makeSource() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getName();
    }

    private static String makeBody() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getParagraphs(1, 1);
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

