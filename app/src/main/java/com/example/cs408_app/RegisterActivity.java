package com.example.cs408_app;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Model.Response;
import com.example.cs408_app.Model.User;
import com.example.cs408_app.Config.Constants;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    // UI references
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mPhoneNumberView;
    private View mProgressView;
    private View mRegisterFormView;

    // Server references
    private Retrofit retrofit;
    private CS4CSApi cs4csApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the register form
        mFirstNameView = findViewById(R.id.first_name);
        mLastNameView = findViewById(R.id.last_name);
        mEmailView = findViewById(R.id.email);
        mPhoneNumberView = findViewById(R.id.phone_number);

        Button mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptRegister();

            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        // Build the Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.server_ip + Constants.server_port)
                .addConverterFactory(GsonConverterFactory.create()) // convert request inputs into JSON, and recognize response outputs as JSON.
                .build();

        cs4csApi = retrofit.create(CS4CSApi.class); // indicate the interface we are going to use

    }

    private void attemptRegister() {

        // Reset errors
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mPhoneNumberView.setError(null);

        // Store values at the time of the register attempt
        String name = mFirstNameView.getText().toString() + " " + mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String phone_number = mPhoneNumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid phone number (later)

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            userRegisterTaskExecute(name, email, phone_number);
        }
    }

    private void userRegisterTaskExecute(final String name, final String email, final String phone_number) {

        User user = new User(name, email, phone_number);

        Call<Response> call = cs4csApi.register(user);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                // if the response is successful
                if (response.isSuccessful() && response.body() != null && response.code() == 201) {
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    // Start ConfirmationActivity
                    // and toss some values; name, email, phone_number
                    Intent intent = new Intent(RegisterActivity.this, ConfirmationActivity.class)
                            .putExtra("name", name)
                            .putExtra("email", email)
                            .putExtra("phone_number", phone_number);
                    startActivity(intent);
                }

                // if the response is not successful (then app receives intended error message)
                else if (!response.isSuccessful() && response.errorBody() != null) {

                    // Manually extract an error message from JSON response
                    // ([Not important] Due to unknown reason, if the response was not successful, Retrofit ignore the explicit error message in the JSON response.
                    // Thus, we need to manually extract the error message in the JSON response)
                    Converter<ResponseBody, Response> errorConverter =
                            retrofit.responseBodyConverter(Response.class, new Annotation[0]);

                    try {
                        Response error = errorConverter.convert(response.errorBody());
                        // show the error message
                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Something wrong, please try again !", Toast.LENGTH_LONG).show();
                    }

                    // Refresh this activity
                    // remove the activity stack ("go back" button will close the app, not return to register activity)
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }

                else {
                    Toast.makeText(RegisterActivity.this, "Unknown error" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) { // even the request is not succeed

                Log.e("Server Connection Fail", t.getLocalizedMessage());
                Toast.makeText(RegisterActivity.this, "Server Connection Fail", Toast.LENGTH_LONG).show();

                // Refresh this activity
                // remove the activity stack ("go back" button will close the app, not return to register activity)
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
