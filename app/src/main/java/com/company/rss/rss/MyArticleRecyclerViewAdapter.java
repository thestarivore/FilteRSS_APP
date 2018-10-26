package com.company.rss.rss;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.rss.rss.ArticleFragment.OnListFragmentInteractionListener;
import com.company.rss.rss.models.ArticleContent;
import com.company.rss.rss.models.ArticleContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyArticleRecyclerViewAdapter extends RecyclerView.Adapter<MyArticleRecyclerViewAdapter.ViewHolder> {

    private final List<ArticleContent.Article> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyArticleRecyclerViewAdapter(List<ArticleContent.Article> articles, OnListFragmentInteractionListener listener) {
        mValues = articles;
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
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);

        holder.mExcerptView.setText(mValues.get(position).excerpt);
        holder.mSubView.setText(mValues.get(position).sub);
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
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mExcerptView;
        public final TextView mSubView;
        public final ImageView mImageView;

        public ArticleContent.Article mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.textViewTitle);
            mExcerptView = (TextView) view.findViewById(R.id.textViewExcerpt);
            mSubView = (TextView) view.findViewById(R.id.textViewSub);
            mImageView = (ImageView) view.findViewById(R.id.imageViewImage);
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
