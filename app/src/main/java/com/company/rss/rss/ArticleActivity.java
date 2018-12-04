package com.company.rss.rss;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.rss.rss.helpers.DownloadImageTask;
import com.company.rss.rss.models.Article;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ArticleActivity extends AppCompatActivity {

    public final static String logTag = "RSSLOG";
    private boolean fabVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        // INPUT DATA
        Intent intent = getIntent();
        final Article article = (Article) intent.getSerializableExtra(ArticlesListActivity.EXTRA_ARTICLE);

        String articleImage = article.getImage();
        String articleSubtitle = article.getSource();
        String articleTitle = article.getTitle();
        String articleBody = article.getBody();
        int readingTime = article.getReadingTime();

        // SETTERS
        ImageView articleImageView = (ImageView) findViewById(R.id.imageViewArticleImage);
        new DownloadImageTask(articleImageView)
                .execute(article.getImage());
        // Set the image to full size of the viewport
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        articleImageView.getLayoutParams().height = size.y / 2;

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

        // EVENTS LISTENER
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabFeedbackButtonArticle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(ArticleActivity.this);
                final View dialogView = LayoutInflater.from(ArticleActivity.this).inflate(R.layout.dialog_feedback_layout, null);
                builder.setView(dialogView);
                final AlertDialog feedbackDialog = builder.create();
                dialogView.findViewById(R.id.linearLayoutFeedbackGood).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendArticlesFeedback(article, 1);
                        Snackbar.make(view, R.string.thank_you_feedback_submit, Snackbar.LENGTH_LONG).show();
                        feedbackDialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.linearLayoutFeedbackAverage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendArticlesFeedback(article, 0);
                        Snackbar.make(view, R.string.thank_you_feedback_submit, Snackbar.LENGTH_LONG).show();
                        feedbackDialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.linearLayoutFeedbackBad).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendArticlesFeedback(article, -1);
                        Snackbar.make(view, R.string.thank_you_feedback_submit, Snackbar.LENGTH_LONG).show();
                        feedbackDialog.dismiss();
                    }
                });
                feedbackDialog.show();
            }
        });

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

                if (!fabVisible && percentageScrolled >= 70) {
                    fab.setVisibility(View.VISIBLE);
                    fabVisible = true;

                    sendArticlesRead(article);
                }
            }
        });

    }

    private void sendArticlesRead(Article article) {
        // TODO: call the API and increment the article reads counter
        Log.v(ArticleActivity.logTag, "Sending article count increment for " + article.toString());

    }

    private void sendArticlesFeedback(Article article, int i) {
        // TODO: call the API and send the feedback
        Log.v(ArticleActivity.logTag, "Sending feedback " + i +  " for " + article.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.itemSaveArticle:
                //add the function to perform here
                return (true);
            case R.id.itemShareArticle:
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
