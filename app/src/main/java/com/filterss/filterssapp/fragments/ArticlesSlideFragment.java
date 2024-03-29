package com.filterss.filterssapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.filterss.filterssapp.R;
import com.filterss.filterssapp.adapters.ArticleRecyclerViewAdapter;
import com.filterss.filterssapp.models.Article;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ArticlesSlideFragment extends Fragment {
    private static final String ARTICLE = "article";
    private Article mArticle;
    private OnFragmentInteractionListener mListener;

    public ArticlesSlideFragment() {
        // Required empty public constructor
    }

    public static ArticlesSlideFragment newInstance(Article article) {
        ArticlesSlideFragment fragment = new ArticlesSlideFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARTICLE, article);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArticle = (Article) getArguments().getSerializable(ARTICLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_slide_single, container, false);
        TextView articleTitle = view.findViewById(R.id.textViewSliderArticleTitle);
        TextView articleSource = view.findViewById(R.id.textViewSliderArticleSource);
        TextView articlePubDateTextView = view.findViewById(R.id.textViewSliderArticlePubDate);
        ImageView articleImage = view.findViewById(R.id.imageViewArticleSlider);
        ImageView articleFeedIcon = view.findViewById(R.id.imageViewArticleFeedIcon);

        if (mArticle != null) {
            articleTitle.setText(mArticle.getTitle());

            if (mArticle.getFeedObj() != null) {
                articleSource.setText(mArticle.getFeedObj().getTitle());

                String feedIcon = mArticle.getFeedObj().getIconURL();
                if (feedIcon == null || feedIcon.isEmpty()) {
                    articleFeedIcon.setVisibility(View.GONE);
                } else {
                    Picasso.get().load(feedIcon).into(articleFeedIcon);
                }
            }

            String imgLink = mArticle.getImgLink();
            if (imgLink != null && !imgLink.isEmpty()) {
                Picasso.get()
                        .load(imgLink)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.ic_rss_feed_white_24dp)
                        .error(R.drawable.ic_error_outline_white_24dp)
                        .noFade()
                        .into(articleImage);
            }

            String pubDate = mArticle.getPubDateString("dd-MM-yyyy");
            if (pubDate == null || pubDate.isEmpty()) {
                articlePubDateTextView.setVisibility(View.GONE);
            } else {
                Date articlePubDate = mArticle.getPubDate();
                pubDate = ArticleRecyclerViewAdapter.formatDatesDiff(getContext(), articlePubDate);
                articlePubDateTextView.setText(pubDate);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onFragmentInteraction(mArticle);
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Article article);
    }
}