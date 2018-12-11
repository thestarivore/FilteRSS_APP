package com.company.rss.rss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.User;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.UserCallback;

import java.util.List;

import static com.company.rss.rss.ArticlesListActivity.EXTRA_ARTICLE;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_USER = "com.rss.rss.USER";
    private static final String TAG = "LoginActivity";
    private Button loginButton;
    private TextView signUpTextView;
    private TextView emailText;
    private TextView passwordText;
    private RESTMiddleware api;
    private User loggedUser;
    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signupTextView);
        emailText = findViewById(R.id.loginMailEditText);
        passwordText = findViewById(R.id.loginPasswordEditText);

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        final boolean userAuthDone = false;

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.authenticating));
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        //Authentication
        api.getUserAuthentication(email, password, new UserCallback() {
            @Override
            public void onLoad(List<User> users) {
                for(User user: users){
                    Log.d(TAG, "\nUser authentication");
                    Log.d(TAG, "\nUser: " + user.getId() +  ", " + user.getName() + ", " + user.getSurname() + ", " + user.getEmail() + ", " + user.getPassword());
                }

                //If there is an User in the list, then the authentication was successful
                //TODO: Persist the User's informations (we may need it's ID later)
                if(users.isEmpty() == false) {
                    //Get Authenticated User
                    loggedUser = users.get(0);

                    //Auth successful
                    onLoginSuccess();
                }
                else
                    onLoginFailed();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "\nFailure on: getUserAuthentication");
                onLoginFailed();
                progressDialog.dismiss();
            }
        });

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        startArticleListActivity();
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private void startArticleListActivity() {
        Intent intent = new Intent(this, ArticlesListActivity.class);
        intent.putExtra(EXTRA_USER, loggedUser);
        startActivity(intent);
    }
}
