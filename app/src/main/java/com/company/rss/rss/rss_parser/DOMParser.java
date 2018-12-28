package com.company.rss.rss.rss_parser;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.RSSFeed;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
import com.joestelmach.natty.Parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parses an RSS feed and adds the information to a new
 * RSSFeed object. Has the ability to report progress to a
 * ProgressBar if one is passed to the constructor.
 */
public class DOMParser {
    //TITLE:"title"
    private ArrayList<String> titleList = new ArrayList<String>() {{
        add("title");
    }};
    //DESCRIPTION:"description", "content:encoded", "content", "summary"(if no other)
    private ArrayList<String> descriptionList = new ArrayList<String>() {{
        add("description");
        add("content:encoded");
        add("content");
        add("summary");
    }};
    //PUB_DATE:"pubDate", "published", "dc:date", "a10:updated"
    private ArrayList<String> pubDateList = new ArrayList<String>() {{
        add("pubDate");
        add("published");
        add("dc:date");
        add("a10:updated");
    }};
    //AUTHOR:"author", "dc:creator", "itunes:author"
    private ArrayList<String> authorList = new ArrayList<String>() {{
        add("author");
        add("dc:creator");
        add("itunes:author");
    }};
    //ARTICLE_LINK:"link"
    private ArrayList<String> linkList = new ArrayList<String>() {{
        add("link");
    }};
    //THUMBNAIL_IMAGE_LINK:"thumbnail", "thumb"
    private ArrayList<String> thumbnailList = new ArrayList<String>() {{
        add("thumbnail");
        add("thumb");
    }};
    //COMMENTS_LINK:"comments", "wfw:commentRss"(rss-comments, use if no normal link for comments)
    private ArrayList<String> commentsList = new ArrayList<String>() {{
        add("comments");
        add("wfw:commentRss");
    }};

    //SingleTAG
    //THUMBNAIL_IMAGE_LINK:"media:thumbnail"(url=), "media:content"(url=), "enclosure"(url=)
    private ArrayList<String> thumbnailSingleTagList = new ArrayList<String>() {{
        add("media:thumbnail");
        add("media:content");
        //add("enclosure");
    }};

    // Create a new RSS feed
    private RSSFeed feed = new RSSFeed();

    public RSSFeed parseXML(String feedURL) {
        String newFeedURL;

        // Create a new URL
        URL url = null;
        try {
            //Convert from HTTP to HTTPS if it isn't already
            /*if(URLUtil.isHttpUrl(feedURL)) {
                newFeedURL = feedURL.replaceAll("http", "https");
            }
            else {
                newFeedURL = feedURL;
            }*/
            newFeedURL = feedURL;

            // Find the new URL from the given URL
            url = new URL(newFeedURL);
        } catch (MalformedURLException e) {
            // Throw an exception
            e.printStackTrace();
        }

        try {
            // Create a new DocumentBuilder
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            //Create an URL Connection
            URLConnection urlConnection = url.openConnection();

            // Parse the XML
            //Document doc = builder.parse(new InputSource(url.openStream()));
            Document doc = builder.parse(new InputSource(urlConnection.getInputStream()));

            // Normalize the data
            doc.getDocumentElement().normalize();

            // Get all <item> OR <entry> tags.
            NodeList list = doc.getElementsByTagName("item");
            if(list.getLength() == 0)
                list = doc.getElementsByTagName("entry");

            // Get size of the list
            int length = list.getLength();

            //Measure the execution time of the parsing
            long startTime = System.nanoTime();

            // For all the items in the feed
            for (int i = 0; i < length; i++) {
                // Create a new node of the first item
                Node currentNode = list.item(i);
                // Create a new RSS item
                Article article = new Article();

                // Get the child nodes of the first item
                NodeList nodeChild = currentNode.getChildNodes();
                // Get size of the child list
                int cLength = nodeChild.getLength();

                // For all the children of a node
                for (int j = 1; j < cLength; j = j + 2) {
                    // Get the name of the child
                    String nodeName = nodeChild.item(j).getNodeName(), nodeString = null;
                    // If there is at least one child element
                    if(nodeChild.item(j).getFirstChild() != null){
                        // Set the string to be the value of the node
                        nodeString = nodeChild.item(j).getFirstChild().getNodeValue();
                    }
                    // If the node string value isn't null
                    if (nodeString != null) {
                        // Set the Title
                        if (titleList.contains(nodeName)) {
                            //if(article.getTitle() == null)
                                article.setTitle(nodeString);
                        }
                        // Set the Description ("description", "content:encoded", "content", "summary"(if no other))
                        else if(descriptionList.contains(nodeName)) {
                            if(article.getDescription() == null)
                                article.setDescription(nodeString.replaceAll("\\<[^>]*>",""));
                        }
                        // Set the PublicationDate ("pubDate", "published", "dc:date", "a10:updated")
                        else if (pubDateList.contains(nodeName)) {
                            if (article.getPubDate() == null)
                                article.setPubDate(parseDateFromString(nodeString));
                        }
                        // Set the Author ("author", "dc:creator", "itunes:author")
                        else if (authorList.contains(nodeName)) {
                            //if (article.getAuthor() == null)
                                article.setAuthor(nodeString);
                        }
                        // Set the Article's Link
                        else if (linkList.contains(nodeName)){
                            //if (article.getLink() == null)
                                article.setLink(nodeString);
                        }
                        // Set the Thumbnail/ImageLink  ("thumbnail", "thumb")
                        else if (thumbnailList.contains(nodeName)){
                            //if (article.getImgLink() == null)
                                article.setImgLink(nodeString);
                        }
                        // Set the Article's Comments   ("comments", "wfw:commentRss")
                        else if (commentsList.contains(nodeName)){
                            if (article.getComment() == null)
                                article.setComment(nodeString);
                        }
                    }
                    // Self closing TAGs with no node string value
                    else{
                        // Set the Thumbnail/ImageLink  ("media:thumbnail"(url=), "media:content"(url=), "enclosure"(url=))
                        if (thumbnailSingleTagList.contains(nodeName)){
                            //if (article.getImgLink() == null)
                                article.setImgLink(nodeChild.item(j).getAttributes().getNamedItem("url").getNodeValue());
                        }
                    }
                }
                // Add the new item to the RSS feed
                feed.addItem(article);
            }
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            System.out.println("DOM_Parsing Duration("+feedURL+"):  " + duration / 1000000 + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return the feed
        return feed;
    }


    /**
     * Parse a Date from a String using multiple matching patterns(Natty library)
     * @param candidate String to parse
     * @return Date object or null if the parsing failed
     */
    private Date parseDateFromString(String candidate){
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(candidate);
        for(DateGroup group:groups) {
            List<Date> dates = group.getDates();
            return dates.get(0);
        }
        return  null;
    }
}
