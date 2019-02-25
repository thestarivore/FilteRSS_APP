package com.filterss.filterssapp;

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

import com.filterss.filterssapp.models.User;
import com.filterss.filterssapp.persistence.UserPrefs;
import com.filterss.filterssapp.restful_api.RESTMiddleware;
import com.filterss.filterssapp.restful_api.callbacks.UserCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private User loggedUser;

    private Button loginButton;
    private TextView emailText;
    private TextView passwordText;
    private static final int REQUEST_SIGNUP = 0;
    private static final int RC_SIGN_IN_GOOGLE = 1;
    private Context context;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        //Verifies if this Activity was opened after a failed authentication
        Intent intent = getIntent();
        boolean persistedAuthFailed = intent.getBooleanExtra("authFailed", false);
        if (persistedAuthFailed == true) {
            Snackbar.make(findViewById(android.R.id.content), R.string.authentication_failed, Snackbar.LENGTH_LONG).show();
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
                Log.d(ArticleActivity.logTag + ":" + TAG, "Starting the Sign up activity...");

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signInGoogle();
                        break;
                }
            }
        });

        // Set the dimensions of the sign-in button.
        signInButton.setSize(SignInButton.SIZE_WIDE);

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        //Get a SharedPreferences instance
        UserPrefs prefs = new UserPrefs(context);

        //Get the User Logged in
        loggedUser = prefs.retrieveUser();
        //Skip Login Activity if User already persisted
        if (loggedUser != null && persistedAuthFailed == false) {
            onLoginSuccess();
        } else if (googleAccount != null) {
            // login with Google
            onLoginSuccess();
        }

    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void handleSignInResultGoogle(Task<GoogleSignInAccount> completedTask) {
        try {
            // Signed in successfully in Google, show authenticated UI.
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.authenticating));
            progressDialog.show();

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Log.d(ArticleActivity.logTag + ":" + TAG, "Authenticating Google user: " + idToken);

            // send ID Token to server and validate
            api.getUserAuthenticationGoogle(idToken, new UserCallback() {
                @Override
                public void onLoad(List<User> users) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Returned users" + users);
                    if (!users.isEmpty()) {
                        loggedUser = users.get(0);
                        Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser: " + loggedUser.getId() + ", " + loggedUser.getName() + ", "
                                + loggedUser.getSurname() + ", " + loggedUser.getEmail() + ", " + loggedUser.getPassword());

                        //Get a SharedPreferences instance
                        UserPrefs prefs = new UserPrefs(context);

                        //Persist the User Logged in
                        prefs.storeUser(loggedUser);
                        onLoginSuccess();
                    } else {
                        onLoginFailed();
                    }
                    progressDialog.dismiss();

                }

                @Override
                public void onFailure() {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserAuthenticationGoogle");
                    progressDialog.dismiss();
                    onLoginFailed();
                }
            });

            //onLoginSuccess();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(ArticleActivity.logTag + ":" + TAG, "Google SignIn, signInResult:failed code=" + e.getStatusCode());

            Snackbar.make(findViewById(android.R.id.content), R.string.login_failed, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Login procedure
     */
    public void login() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Logging in user...");

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
        Log.d(ArticleActivity.logTag + ":" + TAG, "Authenticating user...");
        api.getUserAuthentication(email, password, new UserCallback() {
            @Override
            public void onLoad(List<User> users) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser authentication " + users.size());

                //Get logged user
                if (users.isEmpty() == false) {
                    loggedUser = users.get(0);
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser: " + loggedUser.getId() + ", " + loggedUser.getName() + ", "
                            + loggedUser.getSurname() + ", " + loggedUser.getEmail() + ", " + loggedUser.getPassword());

                    //Get a SharedPreferences instance
                    UserPrefs prefs = new UserPrefs(context);

                    //Persist the User Logged in
                    prefs.storeUser(loggedUser);
                    onLoginSuccess();
                } else {
                    onLoginFailed();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserAuthentication");
                progressDialog.dismiss();
                onLoginFailed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {

            if (resultCode == RESULT_OK) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from Signup activity with RESULT_OK");
                Snackbar.make(findViewById(android.R.id.content), R.string.registration_completed_login, Snackbar.LENGTH_LONG).show();
            } else {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from Signup activity without RESULT_OK");
            }

        } else if (requestCode == RC_SIGN_IN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResultGoogle(task);
        }

    }

    @Override
    public void onBackPressed() {
        // disable going back to the LoadingActivity
        moveTaskToBack(true);
    }

    private void startLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("logged-user", loggedUser);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void onLoginSuccess() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Login success");
        loginButton.setEnabled(true);

        startLoadingActivity();
    }

    public void onLoginFailed() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Login failed");

        Snackbar.make(findViewById(android.R.id.content), R.string.login_failed, Snackbar.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Validating user...");

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
