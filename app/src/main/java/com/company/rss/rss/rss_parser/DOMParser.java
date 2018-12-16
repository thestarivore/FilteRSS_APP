package com.company.rss.rss.rss_parser;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.RSSFeed;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parses an RSS feed and adds the information to a new
 * RSSFeed object. Has the ability to report progress to a
 * ProgressBar if one is passed to the constructor.
 */
public class DOMParser {

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

			// For all the items in the feed
			for (int i = 0; i < length; i++) {
				// Create a new node of the first item
				Node currentNode = list.item(i);
				// Create a new RSS item
				Article item = new Article();

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
						if ("title".equals(nodeName)) {
							item.setTitle(nodeString);
						}
						// Set the Description
						else if ("content:encoded".equals(nodeName)) {
							item.setDescription(nodeString);
						}
						// Set the PublicationDate
						else if ("pubDate".equals(nodeName)) {
							item.setPubDate(new Date(nodeString.replace(" +0000", "")));
						}
						// Set the Author
						else if ("author".equals(nodeName) || "dc:creator".equals(nodeName)) {
							item.setAuthor(nodeString);
						}
						// Set the Article's Link
						else if ("link".equals(nodeName)){
							item.setLink(nodeString);
						}
						// Set the Thumbnail/ImageLink
						else if ("thumbnail".equals(nodeName)){
							item.setImgLink(nodeString);
						}
                        // Set the Article's Comments
                        else if ("comments".equals(nodeName)){
                            item.setComment(nodeString);
                        }
					}
                    // Self closing TAGs with no node string value
					else{
                        // Set the Thumbnail/ImageLink
						if ("media:thumbnail".equals(nodeName)){
                            item.setImgLink(nodeChild.item(j).getAttributes().getNamedItem("url").getNodeValue());
                        }
                    }
				}
				// Add the new item to the RSS feed
				feed.addItem(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Return the feed
		return feed;
	}
}
