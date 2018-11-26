package com.company.rss.rss;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.company.rss.rss.adapters.ArticleSlidePagerAdapter;
import com.company.rss.rss.adapters.ExpandableListAdapter;
import com.company.rss.rss.fragments.ArticlesListFragment;
import com.company.rss.rss.fragments.ArticlesSlideFragment;
import com.company.rss.rss.models.Article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ArticlesListActivity extends AppCompatActivity implements ArticlesListFragment.OnListFragmentInteractionListener, ArticlesSlideFragment.OnFragmentInteractionListener {

    // TODO: refactor this
    private DrawerLayout drawerLayout;
    public static final String EXTRA_ARTICLE = "com.rss.rss.ARTICLE";
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);

        // DRAWER AND TOOLBAR
        // Left Menu
        expListView = (ExpandableListView) findViewById(R.id.exp_list_view_article);
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id) {

                long packedPosition = expListView.getExpandableListPosition(position);

                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    // multifeeds title long clicked
                    if(listDataHeader.get(groupPosition).equals(getString(R.string.multifeeds))){
                        startMultifeedManagerActivity();
                    }
                }
                /*else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    onChildLongClick(groupPosition, childPosition);
                }*/

                return false;
            }
        });

        // Drawer
        drawerLayout = findViewById(R.id.drawer_layout_articles_list);
        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
        Toolbar toolbar = findViewById(R.id.articles_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);


        // TOP ARTICLES - SLIDER
        // Set the slider to half the size of the viewport
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pagerArticles);
        viewPager.getLayoutParams().height = size.y / 3;

        // Create mock articles for top articles
        List<Article> topArticles = Article.generateMockupArticle(6);

        pager = (ViewPager) findViewById(R.id.pagerArticles);
        pagerAdapter = new ArticleSlidePagerAdapter(getSupportFragmentManager(), topArticles);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1, true);
        pager.setClipToPadding(false);
        pager.setPadding(0, 0, 60, 0);
        pager.setPageMargin(0);

        /*// Show CollapsingToolbarLayout Title only when collapsed
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayoutArticlesList);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.AppBarLayoutArticlesList);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Title");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });*/

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // TODO: retrieve multifeed and collections

        listDataHeader.add((String)getText(R.string.multifeeds));
        listDataHeader.add((String)getText(R.string.collections));

        List<String> multifeeds = new ArrayList<String>();
        multifeeds.add("News");
        multifeeds.add("Politics");

        List<String> collections = new ArrayList<String>();
        collections.add("Read it later");
        collections.add("Saved");
        collections.add("Interesting things");

        listDataChild.put(listDataHeader.get(0), multifeeds);
        listDataChild.put(listDataHeader.get(1), collections);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_articles_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.itemSearchArticleList:
                Intent intent = new Intent(this, FeedsSearchActivity.class);
                startActivity(intent);
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Handles the interactions with the list, click
     */
    @Override
    public void onListFragmentInteractionClick(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        startArticleActivity(article);
    }

    /*
    Handles the interactions with the list, swipe
     */
    @Override
    public void onListFragmentInteractionSwipe(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        /* TODO: save article in the read it later collection if not already present
        otherwise remove it from read it later */
        boolean alreadyPresent = false;
        if (alreadyPresent) {
            Toast.makeText(this, R.string.unswipe_to_remove, Toast.LENGTH_LONG).show();
        }
    }

    /*
    Handles the interactions with the top slider
     */
    @Override
    public void onFragmentInteraction(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        startArticleActivity(article);
    }




    private void startArticleActivity(Article article) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(EXTRA_ARTICLE, article);
        startActivity(intent);
    }

    private void startMultifeedManagerActivity() {
        Intent intent = new Intent(this, MultifeedManagerActivity.class);
        startActivity(intent);
    }
}
