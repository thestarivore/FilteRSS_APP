package com.company.rss.rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.models.User;
import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;

public class SignupActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private Button signUpButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Context context;
    private int registeredId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        signUpButton = findViewById(R.id.signUpButton);
        TextView loginTextView = findViewById(R.id.loginTextView);
        nameEditText = findViewById(R.id.signUpNameEditText);
        emailEditText = findViewById(R.id.signUpMailEditText);
        passwordEditText = findViewById(R.id.signUpPasswordEditText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        signUpButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.creating_account));
        progressDialog.show();

        final String name = nameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        //Register User and persist it
        api.registerNewUser(name, "", email, password, new SQLOperationCallback() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                registeredId = sqlOperation.getInsertId();

                User registeredUser = new User(sqlOperation.getInsertId(), name, "", email, password);

                //Get a SharedPreferences instance
                UserPrefs prefs = new UserPrefs( context);

                //Persist the Registred User
                prefs.storeUser(registeredUser);

                onSignupSuccess();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                onSignupFailed();
                progressDialog.dismiss();
            }
        });
        progressDialog.dismiss();
    }


    public void onSignupSuccess() {
        if(registeredId != 0) {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Sign up success");
            signUpButton.setEnabled(true);

            // create a "Read it later" default collection with red color
            api.addUserCollection((String) getText(R.string.read_it_later), registeredId, -65531, new SQLOperationCallback() {
                @Override
                public void onLoad(SQLOperation sqlOperation) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Read it later default collection created");
                }

                @Override
                public void onFailure() {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Read it later default collection NOT created");
                }
            });

            //setResult(RESULT_OK, null);
            startArticlesListActivity();
            finish();
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.signup_failed, Toast.LENGTH_LONG).show();
        signUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameEditText.setError(getText(R.string.name_not_valid));
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getText(R.string.email_not_valid));
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordEditText.setError(getText(R.string.password_not_valid));
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void startArticlesListActivity() {
        Intent intent = new Intent(this, ArticlesListActivity.class);
        startActivity(intent);
    }
}
