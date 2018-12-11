package com.company.rss.rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.rss.rss.models.User;
import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.UserCallback;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String EXTRA_USER = "com.rss.rss.USER";
    private RESTMiddleware api;
    private Button loginButton;
    private TextView emailText;
    private TextView passwordText;
    private static final int REQUEST_SIGNUP = 0;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        //Get a SharedPreferences instance
        UserPrefs prefs = new UserPrefs(context);

        //Get the User Logged in
        User user = prefs.retriveUser();
        //Skip login if User already persisted
        if(user != null) {
            startArticlesListActivity();
        }

        loginButton = findViewById(R.id.loginButton);
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        emailText = findViewById(R.id.loginMailEditText);
        passwordText = findViewById(R.id.loginPasswordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Start the Sign up activity");

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void startArticlesListActivity() {
        Intent intent = new Intent(this, ArticlesListActivity.class);
        startActivity(intent);
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.authenticating));
        progressDialog.show();

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        //Authentication and Persistence of the User
        api.getUserAuthentication(email, password, new UserCallback() {
            @Override
            public void onLoad(List<User> users) {
                for(User user: users){
                    Log.d(TAG, "\nUser authentication");
                    Log.d(TAG, "\nUser: " + user.getId() +  ", " + user.getName() + ", " + user.getSurname() + ", " + user.getEmail() + ", " + user.getPassword());

                    //Get logged user
                    if(users.isEmpty() == false) {
                        User loggedUser = users.get(0);

                        //Get a SharedPreferences instance
                        UserPrefs prefs = new UserPrefs( context);

                        //Persist the User Logged in
                        prefs.storeUser(loggedUser);
                    }
                    progressDialog.dismiss();
                    onLoginSuccess();
                }
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "\nFailure on: getUserAuthentication");
                progressDialog.dismiss();
                onLoginFailed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Returned from Signup activity with RESULT_OK");
                Snackbar.make(findViewById(R.id.login_activity_scroll_view), R.string.registration_completed_login, Snackbar.LENGTH_LONG);
            } else {
                Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Returned from Signup activity without RESULT_OK");
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Login success");
        loginButton.setEnabled(true);
        startArticlesListActivity();
    }

    public void onLoginFailed() {
        Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Login failed");

        Toast.makeText(getBaseContext(), R.string.login_failed, Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getText(R.string.email_not_valid));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getText(R.string.password_not_valid));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

}
