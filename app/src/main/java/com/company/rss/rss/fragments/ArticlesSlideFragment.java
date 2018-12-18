package com.company.rss.rss.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.rss.rss.R;
import com.company.rss.rss.helpers.DownloadImageTask;
import com.company.rss.rss.models.Article;
import com.squareup.picasso.Picasso;

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
        TextView articleTitle = (TextView) view.findViewById(R.id.textViewSliderArticleTitle);
        TextView articleSource = (TextView) view.findViewById(R.id.textViewSliderArticleSource);
        ImageView articleImage = (ImageView) view.findViewById(R.id.imageViewArticleSlider);

        articleTitle.setText(mArticle.getTitle());
        articleSource.setText(mArticle.getLink());

        Picasso.get().load(mArticle.getImgLink()).into(articleImage);

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