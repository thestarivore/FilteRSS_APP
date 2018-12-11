package com.company.rss.rss;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {


    private Button signUpButton;
    private TextView loginTextView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        signUpButton = findViewById(R.id.signUpButton);
        loginTextView = findViewById(R.id.loginTextView);
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

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.creating_account));
        progressDialog.show();

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        signUpButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
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

}
