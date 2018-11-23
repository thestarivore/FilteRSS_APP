package com.company.rss.rss;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;

import java.util.ArrayList;
import java.util.Random;

public class MultifeedManagerActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multifeed);

        final Multifeed[] multifeeds = Multifeed.generateMockupMultifeeds(4);


        final ListView listview = (ListView) findViewById(R.id.listViewMultifeedList);
        final MultifeedManagerActivity.MultifeedListAdapter adapter = new MultifeedManagerActivity.MultifeedListAdapter(this, multifeeds);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Log.v(ArticleActivity.logTag, "Multifeed " + id + " clicked");
                final Multifeed multifeed = (Multifeed) parent.getItemAtPosition(position);

                Log.v(ArticleActivity.logTag, "Multifeed information: " + multifeed.toString());
            }

        });
    }

    private class ViewHolderMultifeed {
        View multifeedViewColor;
        TextView multifeedName;
        TextView multifeedCount;
    }


    private class MultifeedListAdapter extends ArrayAdapter<Multifeed> {
        private final Context context;
        private final Multifeed[] multifeeds;

        public MultifeedListAdapter(Context context, Multifeed[] multifeeds) {
            super(context, -1, multifeeds);
            this.context = context;
            this.multifeeds = multifeeds;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MultifeedManagerActivity.ViewHolderMultifeed viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.multifeed_item, parent, false);

                viewHolder = new MultifeedManagerActivity.ViewHolderMultifeed();
                viewHolder.multifeedViewColor = (View) convertView.findViewById(R.id.viewMultifeedColor);
                viewHolder.multifeedName = (TextView) convertView.findViewById(R.id.textViewMultifeedName);
                viewHolder.multifeedCount= (TextView) convertView.findViewById(R.id.textViewMultifeedCount);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MultifeedManagerActivity.ViewHolderMultifeed) convertView.getTag();
            }

            Multifeed multifeed = multifeeds[position];
            if (multifeed != null) {
                viewHolder.multifeedName.setText(multifeed.getName());

                viewHolder.multifeedCount.setText(String.valueOf(multifeed.getFeedCount()));

                Log.v(ArticleActivity.logTag, "Multifeed color: " + multifeed.getColor());

                viewHolder.multifeedViewColor.setBackgroundColor(multifeed.getColor());
            }
            return convertView;

        }
    }

}

