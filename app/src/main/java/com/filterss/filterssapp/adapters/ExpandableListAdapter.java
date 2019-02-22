package com.filterss.filterssapp.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filterss.filterssapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groups;
    private HashMap<String, List<String>> items;
    private HashMap<String, List<String>> icons;
    private HashMap<String, Integer> colors;
    private int numberOfItems;
    private HashMap<String,Integer>  feedArticlesNumber;

    public ExpandableListAdapter(Context context, List<String> groups,
                                    HashMap<String, List<String>> items,
                                    HashMap<String, List<String>> icons,
                                    HashMap<String, Integer> colors) {
        this.context    = context;
        this.groups     = groups;
        this.items      = items;
        this.icons      = icons;
        this.colors     = colors;
        this.feedArticlesNumber = null;

        //Get the number of elements in the items HashMap
        this.numberOfItems = 0;
        Iterator it = items.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<String> feedTitles = (List<String>) pair.getValue();
            this.numberOfItems += feedTitles.size();
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.items.get(this.groups.get(groupPosition))
                .get(childPosition);
    }

    public Object getChildIcon(int groupPosition, int childPosition) {
        return this.icons.get(this.groups.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_view_item, null);
        }

        //Text view containing the Name of the Child (feed)
        TextView textView = (TextView) convertView.findViewById(R.id.exp_menu_group_item);
        textView.setText(childText);


        //Set The Number of Articles(TextView) associated to each Feed child
        TextView countTextView = (TextView) convertView.findViewById(R.id.exp_menu_group_item_count);
        String feedTitle = (String) getChild(groupPosition, childPosition);
        int numberOfArticles = 0;       //Default: Not known yet
        //If updated, pick the real number of articles, else use the default momentary (it will be called again once the number is updated)
        if(feedArticlesNumber != null && feedArticlesNumber.containsKey(feedTitle)) {
            numberOfArticles = feedArticlesNumber.get(feedTitle);        //Get the number of articles for the feed with the title=feedTitle
        }
        countTextView.setText(String.valueOf(numberOfArticles));

        //Child's Icon
        ImageView feedIcon  = (ImageView) convertView.findViewById(R.id.exp_menu_group_item_icon);
        String iconLink = (String) getChildIcon(groupPosition, childPosition);
        if(!iconLink.isEmpty())
            Picasso.get().load(iconLink).into(feedIcon);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.items.get(this.groups.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_view_group, null);
        }

        /*if (isExpanded) {
            groupHolder.img.setImageResource(R.drawable.group_down);
        } else {
            groupHolder.img.setImageResource(R.drawable.group_up);
        }*/

        /*//Set the ColorBar for each group
        View colorView = (View) convertView.findViewById(R.id.exp_menu_group_colorbar);
        if(colors.get(getGroup(groupPosition)) != null)
            colorView.setBackgroundColor(colors.get(getGroup(groupPosition)));*/

        //Set the Title for each group
        TextView textView = convertView.findViewById(R.id.exp_menu_group_name);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(headerTitle);
        textView.setTextColor(colors.get(getGroup(groupPosition)));

        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /**
     * Sets the number(TextView) of articles for each feed, data mapped in a HashMap passed as argument
     * @param feedArticlesNumberMap HashMap of the feed,number of articles association
     */
    public void updateFeedArticlesNumbers(Map<String,Integer> feedArticlesNumberMap) {
        feedArticlesNumber = (HashMap<String, Integer>) feedArticlesNumberMap;
    }
}
