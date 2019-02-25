package com.filterss.filterssapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.filterss.filterssapp.models.User;
import com.filterss.filterssapp.models.UserData;
import com.filterss.filterssapp.restful_api.LoadUserData;
import com.filterss.filterssapp.restful_api.interfaces.AsyncResponse;
import com.filterss.filterssapp.service.SQLiteService;

import java.util.Random;

public class LoadingActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private String[] loginSentences = {
            "Reticulating splines...",
            "Generating witty dialog...",
            "Swapping time and space...",
            "Spinning violently around the y-axis...",
            "Tokenizing real life...",
            "Bending the spoon...",
            "Filtering morale...",
            "Don't think of purple hippos...",
            "We need a new fuse...",
            "Have a good day...",
            "640K ought to be enough for anybody...",
            "The bits are breeding...",
            "We're building the buildings as fast as we can...",
            "...and enjoy the elevator music...",
            "Please wait while the little elves download your data...",
            "Don't worry - a few bits tried to escape, but we caught them...",
            "Checking the gravitational constant in your locale...",
            "Go ahead - hold your breath!...",
            "The server is powered by a lemon and two electrodes...",
            "Please wait while a larger software vendor in Seattle takes over the world...",
            "We're testing your patience...",
            "As if you had any other choice...",
            "Follow the white rabbit...",
            "While the satellite moves into position...",
            "The bits are flowing slowly today...",
            "Dig on the 'X' for buried treasure... ARRR!...",
            "It's still faster than you could draw it...",
            "My other loading screen is much faster...",
            "Reconfoobling energymotron...",
            "Just count to 10...",
            "Why so serious?...",
            "Updating Updater...",
            "Downloading Downloader...",
            "Debugging Debugger...",
            "Counting backwards from Infinity...",
            "Embiggening Prototypes...",
            "We're making you a cookie...",
            "Creating time-loop inversion field...",
            "Spinning the wheel of fortune...",
            "All I really need is a kilobit...",
            "I feel like I'm supposed to be loading something...",
            "Adjusting flux capacitor...",
            "I swear it's almost done...",
            "Let's take a mindfulness minute...",
            "Keeping all the 1's and removing all the 0's...",
            "Making sure all the i's have dots...",
            "Connecting Neurotoxin Storage Tank...",
            "Granting wishes...",
            "Spinning the hamster...",
            "Load it and they will come...",
            "Convincing AI not to turn evil...",
            "Wait, do you smell something burning?...",
            "Computing the secret to life, the universe, and everything...",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Log.d(ArticleActivity.logTag + ":" + TAG, "Started...");

        Random random = new Random();
        TextView loadingTextView = findViewById(R.id.loadingTextView);
        loadingTextView.setText(loginSentences[random.nextInt(loginSentences.length - 1)]);

        Intent intent = getIntent();
        User loggedUser = (User) intent.getSerializableExtra("logged-user");

        //Get SQLite Service instance
        final long startTime = System.nanoTime();
        SQLiteService sqLiteService = SQLiteService.getInstance(this);
        //Get a UserData instance
        final UserData userData = UserData.getInstance();

        /*
        //Get all the articles stored locally in the SQLite Database
        sqLiteService.getAllArticles(new ArticleCallback() {
            @Override
            public void onLoad(List<Article> localArticles) {
                if (localArticles != null) {
                    userData.setLocalArticleList(localArticles);
                    long endTimeSQLite = System.nanoTime();
                    long duration = (endTimeSQLite - startTime);  //divide by 1000000 to get milliseconds.
                    Log.d(ArticleActivity.logTag + ":" + TAG, "#TIME: Got Local SQLite Articles in: "+ (duration/1000000) +"ms");
                }
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Failed!");
            }
        });
        */

        //Load user data from the server only if there is internet connection
        if (isNetworkAvailable()) {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Online mode...");

            Log.d(ArticleActivity.logTag + ":" + TAG, "Starting user's data loading...");
            final long startTimeLUD = System.nanoTime();

            //Start an AsyncTask to gather all the User's information before stepping into the main Activity
            new LoadUserData(new AsyncResponse() {
                @Override
                public void processFinish(Integer output) {

                    if (output == LoadUserData.DATA_LOADING_TERMINTAED) {
                        //All the data has been gathered so we can open the main activity
                        Log.d(ArticleActivity.logTag + ":" + TAG, "User's data loaded...");

                        //Time the operation
                        long endTimeLUD = System.nanoTime();
                        long duration = (endTimeLUD - startTimeLUD);  //divide by 1000000 to get milliseconds.
                        Log.d(ArticleActivity.logTag + ":" + TAG, "#TIME: Got Local User DATA from SharedPrefs in: "+ (duration/1000000) +"ms");

                        //Start the ArticlesListActivity on success
                        startArticlesListActivity();
                    } else if (output == LoadUserData.AUTHENTICATION_FAILED) {
                        startLoginActivityOnAuthFailed();
                        Snackbar.make(findViewById(android.R.id.content), R.string.authentication_failed, Snackbar.LENGTH_LONG).show();
                    }

                }
            }, this, loggedUser).execute();
        }
        //Offline Mode
        else {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Offline mode...");
            startArticlesListActivity();

            /*
            Thread waitAllFeedsLoaded = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Wait for the local list of articles to load first in offline mode
                    while (userData.isLocalArticleListLoaded() == false) ;

                    //Then afterwards start the ArticlesList Activity
                    startArticlesListActivity();
                }
            });
            waitAllFeedsLoaded.start();*/
        }
    }

    /**
     * Start ArticlesListActivity
     */
    private void startArticlesListActivity() {
        Intent intent = new Intent(this, ArticlesListActivity.class);
        startActivity(intent);
    }

    /**
     * Start LoginActivity after an authentication failed
     */
    private void startLoginActivityOnAuthFailed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("authFailed", true);
        startActivity(intent);
    }

    /**
     * Query if there is Internet Connection or the device is Offline
     *
     * @return True if there is Internet connection, false if Offline
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
