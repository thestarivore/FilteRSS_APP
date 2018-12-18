package com.company.rss.rss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.company.rss.rss.models.User;
import com.company.rss.rss.restful_api.LoadUserData;
import com.company.rss.rss.restful_api.interfaces.AsyncResponse;

import java.util.Random;

public class LoadingActivity extends AppCompatActivity {
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

        Random random = new Random();
        TextView loadingTextView = findViewById(R.id.loadingTextView);
        loadingTextView.setText(loginSentences[random.nextInt(loginSentences.length-1)]);

        Intent intent = getIntent();
        User loggedUser;
        loggedUser = (User) intent.getSerializableExtra("logged-user");


        //Start an AsyncTask to gather all the User's information before stepping into the main Activity
        new LoadUserData(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                //All the data has been gathered so we can open the main activity
                startArticlesListActivity();

            }
        }, this, loggedUser).execute();
    }

    private void startArticlesListActivity() {
        Intent intent = new Intent(this, ArticlesListActivity.class);
        startActivity(intent);
    }

}
