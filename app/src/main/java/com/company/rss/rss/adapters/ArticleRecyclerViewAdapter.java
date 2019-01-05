package com.company.rss.rss.adapters;


import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.R;
import com.company.rss.rss.fragments.ArticlesListFragment.OnListFragmentInteractionListener;
import com.company.rss.rss.models.Article;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;


public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {
    private final String TAG = getClass().getName();
    private final List<Article> mArticles;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private TextView articleSwipeTextView;

    public ArticleRecyclerViewAdapter(List<Article> articles, OnListFragmentInteractionListener listener, Context context) {
        mArticles = articles;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_article, parent, false);

        articleSwipeTextView = view.findViewById(R.id.articleSwipeTextView);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mArticles.get(position);

        holder.mArticleColorView.setBackgroundColor(mArticles.get(position).getFeed().getMultifeed().getColor());
        holder.mTitleView.setText(mArticles.get(position).getTitle());

        String description = mArticles.get(position).getDescription();

        if (description == null || description.isEmpty() || description.length() < 10) {
            holder.mDescriptionView.setVisibility(View.GONE);
        } else {
            holder.mDescriptionView.setText(description);
        }

        String pubDate = mArticles.get(position).getPubDateString("dd-MM-yyyy");
        if (pubDate == null || pubDate.isEmpty()) {
            holder.mPubDateView.setVisibility(View.GONE);
        } else {
            Date articlePubDate = mArticles.get(position).getPubDate();
            pubDate = computeDaysDiff(mContext, pubDate, articlePubDate);

            pubDate = " // " + pubDate;
            holder.mPubDateView.setText(pubDate);
        }

        holder.mFeedNameView.setText(mArticles.get(position).getFeed().getTitle());

        String imgLink = mArticles.get(position).getImgLink();
        if (imgLink == null || imgLink.isEmpty()) {
            holder.mImageView.setVisibility(View.GONE);
        } else {
            Picasso.get().load(imgLink).into(holder.mImageView);
        }

        String feedIcon = mArticles.get(position).getFeed().getIconURL();
        if (feedIcon == null || feedIcon.isEmpty()) {
            holder.mFeedIcon.setVisibility(View.GONE);
        } else {
            Picasso.get().load(feedIcon).into(holder.mFeedIcon);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mImageView.setClipToOutline(true);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteractionClick(holder.mItem);
                }
            }
        });
    }

    public void setCollectionMode(boolean collectionMode) {
        if (collectionMode) {
            articleSwipeTextView.setText(R.string.removed_from_collection);
        } else {
            articleSwipeTextView.setText(R.string.swipe_to_save);
        }
    }

    static public String computeDaysDiff(Context context, String pubDate, Date articlePubDate) {
        Date nowDate = new Date();

        long diff = nowDate.getTime() - articlePubDate.getTime();

        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        if (diffDays == 0) pubDate = context.getString(R.string.today);
        else if (diffDays == 1) pubDate = context.getString(R.string.yesterday);
        else if (diffDays > 1) pubDate = diffDays + " " + context.getString(R.string.days);
        return pubDate;
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final View mArticleColorView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final TextView mFeedNameView;
        public final TextView mPubDateView;
        public final ImageView mImageView;
        public final ImageView mFeedIcon;
        public LinearLayout viewBackground, viewForeground;

        public Article mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mArticleColorView = view.findViewById(R.id.viewArticleColor);
            mTitleView = view.findViewById(R.id.textViewTitle);
            mDescriptionView = view.findViewById(R.id.textViewExcerpt);
            mFeedNameView = view.findViewById(R.id.textViewFeedName);
            mImageView = view.findViewById(R.id.imageViewImage);
            mFeedIcon = view.findViewById(R.id.imageViewFeedIcon);
            mPubDateView = view.findViewById(R.id.textViewPubDate);

            viewBackground = view.findViewById(R.id.articleListSingleBackground);
            viewForeground = view.findViewById(R.id.articleListSingleForeground);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mView=" + mView +
                    ", mTitleView=" + mTitleView +
                    ", mDescriptionView=" + mDescriptionView +
                    ", mFeedNameView=" + mFeedNameView +
                    ", mImageView=" + mImageView +
                    ", mItem=" + mItem +
                    '}';
        }
    }


}
