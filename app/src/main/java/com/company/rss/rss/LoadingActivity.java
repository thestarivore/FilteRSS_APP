package com.company.rss.rss;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.company.rss.rss.models.User;
import com.company.rss.rss.restful_api.LoadUserData;
import com.company.rss.rss.restful_api.interfaces.AsyncResponse;

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
        loadingTextView.setText(loginSentences[random.nextInt(loginSentences.length-1)]);

        Intent intent = getIntent();
        User loggedUser = (User) intent.getSerializableExtra("logged-user");

        //Load user data from the server only if there is internet connection
        if (isNetworkAvailable()) {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Starting user's data loading...");
            //Start an AsyncTask to gather all the User's information before stepping into the main Activity
            new LoadUserData(new AsyncResponse() {
                @Override
                public void processFinish(Integer output) {
                    if (output == LoadUserData.DATA_LOADING_TERMINTAED) {
                        //All the data has been gathered so we can open the main activity
                        Log.d(ArticleActivity.logTag + ":" + TAG, "User's data loaded...");
                        startArticlesListActivity();
                    } else if (output == LoadUserData.AUTHENTICATION_FAILED) {
                        startLoginActivityOnAuthFailed();
                        Snackbar.make(findViewById(android.R.id.content), R.string.authentication_failed, Snackbar.LENGTH_LONG).show();
                    }
                }
            }, this, loggedUser).execute();
        }
        //Offline Mode
        else{
            startArticlesListActivity();
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
     * @return True if there is Internet connection, false if Offline
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
