package com.company.rss.rss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.rss.rss.R;
import com.company.rss.rss.models.Feed;

import java.util.List;

public class FeedsListAdapter extends ArrayAdapter<Feed> {
    private final Context context;
    private final List<Feed> feeds;
    private final boolean removeIcon;

    public FeedsListAdapter(Context context, List<Feed> feeds, boolean removeIcon) {
        super(context, -1, feeds);
        this.context = context;
        this.feeds = feeds;
        this.removeIcon = removeIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderFeed viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.feeds_search_item, parent, false);

            viewHolder = new ViewHolderFeed();
            viewHolder.feedName = (TextView) convertView.findViewById(R.id.textViewFeedsSearchName);
            viewHolder.feedCategory = (TextView) convertView.findViewById(R.id.textViewFeedsSearchCategory);
            viewHolder.feedIcon = (ImageView) convertView.findViewById(R.id.imageViewFeedsSearchIcon);
            viewHolder.feedActionIcon = (ImageView) convertView.findViewById(R.id.imageViewFeedsSearchActionIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderFeed) convertView.getTag();
        }

        Feed feed = feeds.get(position);
        if (feed != null) {
            viewHolder.feedName.setText(feed.getTitle());
            viewHolder.feedCategory.setText(feed.getCategory());
            if(removeIcon) viewHolder.feedActionIcon.setRotation(45); // Show delete icon
        }
        return convertView;

    }

    public class ViewHolderFeed {
        TextView feedName;
        TextView feedCategory;
        ImageView feedIcon;
        ImageView feedActionIcon;
    }
}


