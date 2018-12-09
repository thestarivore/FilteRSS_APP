package com.company.rss.rss.adapters;


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
import com.company.rss.rss.helpers.DownloadImageTask;
import com.company.rss.rss.models.Article;

import java.util.List;


public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {

    private final List<Article> mArticles;
    private final OnListFragmentInteractionListener mListener;

    public ArticleRecyclerViewAdapter(List<Article> articles, OnListFragmentInteractionListener listener) {
        mArticles = articles;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mArticles.get(position);
        holder.mTitleView.setText(mArticles.get(position).getTitle());

        holder.mExcerptView.setText(mArticles.get(position).getExcerpt());
        holder.mSubView.setText(mArticles.get(position).getLink());

        // show The Image in a ImageView
        new DownloadImageTask(holder.mImageView)
                .execute(mArticles.get(position).getThumbnail());

        holder.mImageView.setClipToOutline(true);

        holder.mTitleView.post(new Runnable() {
            @Override
            public void run() {
                Log.v(ArticleActivity.logTag, String.valueOf(holder.mTitleView.getLineCount()));
                if(holder.mTitleView.getLineCount() == 1){
                    Log.v(ArticleActivity.logTag, "Increase excerpt");
                    holder.mExcerptView.setMaxLines(3);
                }
            }
        });


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

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mExcerptView;
        public final TextView mSubView;
        public final ImageView mImageView;
        public LinearLayout viewBackground, viewForeground;

        public Article mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.textViewTitle);
            mExcerptView = (TextView) view.findViewById(R.id.textViewExcerpt);
            mSubView = (TextView) view.findViewById(R.id.textViewSub);
            mImageView = (ImageView) view.findViewById(R.id.imageViewImage);

            viewBackground = view.findViewById(R.id.articleListSingleBackground);
            viewForeground = view.findViewById(R.id.articleListSingleForeground);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mView=" + mView +
                    ", mTitleView=" + mTitleView +
                    ", mExcerptView=" + mExcerptView +
                    ", mSubView=" + mSubView +
                    ", mImageView=" + mImageView +
                    ", mItem=" + mItem +
                    '}';
        }
    }


}
