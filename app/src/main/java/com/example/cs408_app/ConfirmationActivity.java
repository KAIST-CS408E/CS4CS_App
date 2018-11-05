package com.example.cs408_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.Response;
import com.example.cs408_app.Model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmationActivity extends AppCompatActivity {

    // UI references
    private EditText mCodeView;
    private View mProgressView;
    private View mConfirmFormView;

    // Server references
    private Retrofit retrofit;
    private CS4CSApi cs4csApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_confirmation);

        // Set up the register form
        mCodeView = findViewById(R.id.code);

        Button mConfirmButton = findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptConfirm();

            }
        });

        mConfirmFormView = findViewById(R.id.confirm_form);
        mProgressView = findViewById(R.id.confirm_progress);

        // Build the Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + Constants.server_ip + ":8003")
                .addConverterFactory(GsonConverterFactory.create()) // convert request inputs into JSON, and recognize response outputs as JSON.
                .build();

        cs4csApi = retrofit.create(CS4CSApi.class); // indicate the interface we are going to use

    }

    private void attemptConfirm() {

        mCodeView.setError(null);

        String secret_code = mCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(secret_code)) {
            mCodeView.setError("This field is required");
            focusView = mCodeView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            String name, email, phone_number;
            Bundle extras = getIntent().getExtras();

            if (extras != null) {

                name = extras.getString("name");
                email = extras.getString("email");
                phone_number = extras.getString("phone_number");

                showProgress(true);
                userConfirmTaskExecute(name, email, phone_number, secret_code);

            } else {

                Log.e("Something Wrong", "getIntent Failure");
                Toast.makeText(ConfirmationActivity.this, "Try Register Again", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void userConfirmTaskExecute(String name, String email, String phone_number, String secret_code) {

        User user = new User(name, email, phone_number);
        user.setToken(secret_code);

        Call<Response> call = cs4csApi.register(user);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.code() == 200) {
                        // Start ConfirmationActivity with extra values; name, email, phone_number
                        Toast.makeText(ConfirmationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else if (response.body() != null) {
                    Log.e("Error", response.body().getMessage());
                    Toast.makeText(ConfirmationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("Server Connection Fail", t.getLocalizedMessage());
                Toast.makeText(ConfirmationActivity.this, "Server Connection Fail", Toast.LENGTH_SHORT).show();
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

            mConfirmFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mConfirmFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mConfirmFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mConfirmFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
