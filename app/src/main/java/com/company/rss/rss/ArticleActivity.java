package com.company.rss.rss;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
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

import com.company.rss.rss.helpers.DownloadImageTask;
import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.Collection;

import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ArticleActivity extends AppCompatActivity {

    public final static String logTag = "RSSLOG";
    private boolean fabVisible;
    private Article article;

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
        article = (Article) intent.getSerializableExtra(ArticlesListActivity.EXTRA_ARTICLE);

        String articleImage = article.getImgLink();
        String articleSubtitle = article.getLink();
        String articleTitle = article.getTitle();
        String articleBody = article.getDescription();
        int readingTime = article.getReadingTime();

        // SETTERS
        ImageView articleImageView = (ImageView) findViewById(R.id.imageViewArticleImage);
        new DownloadImageTask(articleImageView)
                .execute(article.getImgLink());
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
        Log.v(ArticleActivity.logTag, "Sending feedback " + i + " for " + article.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSaveArticle:
                showDialogCollectionsList();
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

    private void showDialogCollectionsList() {
        // TODO: get user's collections
        final List<Collection> collections = Collection.generateMockupCollections(5);

        new android.app.AlertDialog.Builder(ArticleActivity.this)
                .setTitle(R.string.dialog_add_article_to_collection)
                .setSingleChoiceItems(Collection.toStrings(collections), -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedIndex) {
                                Log.d(ArticleActivity.logTag, "Collection " + selectedIndex + " clicked, info: " + collections.get(selectedIndex).toString());

                                boolean added = addArticleToCollection(article, collections.get(selectedIndex));

                                if (added) {
                                    Snackbar.make(getCurrentFocus(), R.string.article_added_to_collection, Snackbar.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    Snackbar.make(getCurrentFocus(), R.string.error_adding_article, Snackbar.LENGTH_LONG).show();
                                }

                            }
                        })
                .setPositiveButton(R.string.dialog_add_article_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(ArticleActivity.logTag, "Creating new collection");
                        createNewCollection(new Collection());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(ArticleActivity.logTag, "Dialog closed");
                    }
                })
                .show();
    }

    private void createNewCollection(final Collection collection) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ArticleActivity.this);

        View dialogView = LayoutInflater.from(ArticleActivity.this).inflate(R.layout.dialog_collection_edit, null);

        final TextView collectionTitle = dialogView.findViewById(R.id.editTextCollectionEditTitle);
        final View collectionColor = dialogView.findViewById(R.id.viewCollectionEditColor);

        collectionTitle.setText(collection.getTitle());
        GradientDrawable background = (GradientDrawable) collectionColor.getBackground();
        background.setColor(collection.getColor() == 0 ? Color.BLACK : collection.getColor());

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = collectionTitle.getText().toString();

                // TODO: call the API and create the collection
                collection.setTitle(title);

                Log.v(ArticleActivity.logTag, "Saving collection: " + collection.toString());
            }
        });

        builder.setView(dialogView);
        final android.app.AlertDialog editCollectionDialog = builder.create();

        collectionColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                editCollectionDialog.dismiss();
                new ColorPickerPopup.Builder(ArticleActivity.this)
                        .initialColor(collection.getColor())
                        .enableBrightness(false)
                        .enableAlpha(false)
                        .okTitle(getString(R.string.choose))
                        .cancelTitle(getString(R.string.cancel))
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                collection.setColor(color);
                                createNewCollection(collection);
                            }

                            @Override
                            public void onColor(int color, boolean fromUser) {

                            }
                        });
            }
        });

        editCollectionDialog.show();
    }

    private boolean addArticleToCollection(Article article, Collection collection) {
        // TODO: call the API and add the article to the collection

        // Article added
        Boolean articleAdded = true;
        if (articleAdded)
            return true;
        else
            return false;
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
