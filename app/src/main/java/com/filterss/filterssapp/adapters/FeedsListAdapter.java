package com.filterss.filterssapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.filterss.filterssapp.R;
import com.filterss.filterssapp.models.Feed;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FeedsListAdapter extends ArrayAdapter<Feed> implements Filterable {
    private final Context context;
    private List<Feed> feeds;
    private List<Feed> allFeeds;
    private final boolean removeIcon;
    private Filter filter;


    static class ViewHolderFeed {
        TextView feedName;
        TextView feedCategory;
        ImageView feedIcon;
        ImageView feedActionIcon;
    }
    public FeedsListAdapter(Context context, List<Feed> feeds, boolean removeIcon) {
        super(context, -1, feeds);
        this.context = context;
        this.feeds = new ArrayList<>(feeds);
        this.allFeeds = new ArrayList<>(feeds);
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
            viewHolder.feedName = convertView.findViewById(R.id.textViewFeedsSearchName);
            viewHolder.feedCategory = convertView.findViewById(R.id.textViewFeedsSearchCategory);
            viewHolder.feedIcon = convertView.findViewById(R.id.imageViewFeedsSearchIcon);
            viewHolder.feedActionIcon = convertView.findViewById(R.id.imageViewFeedsSearchActionIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderFeed) convertView.getTag();
        }

        Feed feed = feeds.get(position);

        if (feed != null) {
            viewHolder.feedName.setText(feed.getTitle());
            viewHolder.feedCategory.setText(feed.getCategory());
            if(!feed.getIconURL().isEmpty())
                Picasso.get().load(feed.getVisualURL()).into(viewHolder.feedIcon);
            if (removeIcon) viewHolder.feedActionIcon.setRotation(45); // Show delete icon
        }

        return convertView;

    }


    public void updateFeeds(List<Feed> feeds) {
        this.feeds = new ArrayList<>(feeds);
        this.allFeeds = new ArrayList<>(feeds);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return feeds.size();
    }

    @Override
    public Feed getItem(int location) {
        return feeds.get(location);
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new FeedFilter();
        return filter;
    }
    class FeedFilter extends Filter {



        List<Feed> filteredResult = new ArrayList<>();

        @Override
        protected FilterResults performFiltering(CharSequence query) {

            filteredResult.clear();

            FilterResults results = new FilterResults();

            if (query.length() == 0) {

                filteredResult.addAll(allFeeds);

            } else {

                query = query.toString().toLowerCase();

                List<Feed> feedMatches = new ArrayList<>();

                for (Feed feed : allFeeds) {

                    if (feed.getTitle().toLowerCase().contains(query)) {
                        feedMatches.add(feed);
                    }

                }

                filteredResult.addAll(feedMatches);

            }

            results.count = filteredResult.size();
            results.values = filteredResult;

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            feeds = (ArrayList<Feed>) results.values;
            notifyDataSetChanged();
        }

    }
}


