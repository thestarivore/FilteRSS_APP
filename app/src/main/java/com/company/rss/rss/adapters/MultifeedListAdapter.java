package com.company.rss.rss.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.R;
import com.company.rss.rss.models.Multifeed;

import java.util.List;


public class MultifeedListAdapter extends ArrayAdapter<Multifeed> {
    public List<Multifeed> getItems() {
        return multifeeds;
    }

    private class ViewHolderMultifeed {
        View multifeedViewColor;
        TextView multifeedName;
        TextView multifeedCount;
    }


    private final Context context;
    private final List<Multifeed> multifeeds;

    public MultifeedListAdapter(Context context, List<Multifeed> multifeeds) {
        super(context, -1, multifeeds);
        this.context = context;
        this.multifeeds = multifeeds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderMultifeed viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.multifeed_item, parent, false);

            viewHolder = new ViewHolderMultifeed();
            viewHolder.multifeedViewColor = (View) convertView.findViewById(R.id.viewMultifeedColor);
            viewHolder.multifeedName = (TextView) convertView.findViewById(R.id.textViewMultifeedName);
            viewHolder.multifeedCount= (TextView) convertView.findViewById(R.id.textViewMultifeedCount);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderMultifeed) convertView.getTag();
        }

        Multifeed multifeed = multifeeds.get(position);
        if (multifeed != null) {
            viewHolder.multifeedName.setText(multifeed.getName());
            viewHolder.multifeedCount.setText(String.valueOf(multifeed.getFeedCount()));
            viewHolder.multifeedViewColor.setBackgroundColor(multifeed.getColor());
        }
        return convertView;

    }
}