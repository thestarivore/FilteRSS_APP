package com.company.rss.rss.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.rss.rss.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groups;
    private HashMap<String, List<String>> items;
    private HashMap<String, List<String>> icons;
    private TextView numberOfArticles;

    public ExpandableListAdapter(Context context, List<String> groups,
                                    HashMap<String, List<String>> items,
                                    HashMap<String, List<String>> icons) {
        this.context    = context;
        this.groups     = groups;
        this.items      = items;
        this.icons      = icons;
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

        //Text view containing the number of articles left for each child (feed)
        numberOfArticles = (TextView) convertView.findViewById(R.id.exp_menu_group_item_count);

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

        TextView textView = (TextView) convertView
                .findViewById(R.id.exp_menu_group_name);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(headerTitle);

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

}
