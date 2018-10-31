package com.company.rss.rss;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.company.rss.rss.models.ArticleContent;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ArticleActivity extends AppCompatActivity {

    public final static String logTag = "RSSLOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        // INPUT DATA
        Intent intent = getIntent();
        ArticleContent.Article article = (ArticleContent.Article) intent.getSerializableExtra(ArticlesListActivity.EXTRA_ARTICLE);

        int articleImage = R.drawable.mockimg;
        String articleSubtitle = article.getSource();
        String articleTitle = article.getTitle();
        String articleBody = article.getBody();
        int readingTime = article.getReadingTime();

        // SETTERS
        ImageView articleImageView = (ImageView) findViewById(R.id.imageViewArticleImage);
        articleImageView.setBackgroundResource(articleImage);

        TextView articleSubtitleTextView = (TextView) findViewById(R.id.textViewArticleSubtitle);
        articleSubtitleTextView.setText(articleSubtitle);

        TextView articleTitleTextView = (TextView) findViewById(R.id.textViewArticleTitle);
        articleTitleTextView.setText(articleTitle);

        TextView articleBodyTextView = (TextView) findViewById(R.id.textViewArticleBody);
        articleBodyTextView.setText(articleBody);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            articleBodyTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        TextView articleReadTimeTextView = (TextView) findViewById(R.id.textViewReadTime);
        String articleReadTime = readingTime + "M";
        articleReadTimeTextView.setText(articleReadTime);

        // Scroll progess
        final ScrollView positionScrollView = (ScrollView) findViewById(R.id.scrollViewArticle);
        final ProgressBar positionProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        positionScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = positionScrollView.getScrollY() + positionScrollView.getHeight();
                int maxScroll = positionScrollView.getChildAt(0).getHeight();
                float percentageScrolled = (float) scrollY / (float) maxScroll * 100;
                //Log.v(logTag, Float.toString(percentageScrolled));
                positionProgressBar.setProgress((int) percentageScrolled);
            }
        });

        // EVENTS LISTENER
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.save:
                //add the function to perform here
                return (true);
            case R.id.share:
                //add the function to perform here
                return (true);
            case R.id.about:
                //add the function to perform here
                return (true);
            case R.id.exit:
                //add the function to perform here
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
