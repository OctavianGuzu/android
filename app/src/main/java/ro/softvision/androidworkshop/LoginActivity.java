package ro.softvision.androidworkshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.softvision.androidworkshop.model.GitHub;
import ro.softvision.androidworkshop.model.LoginData;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private TextView mUsername;
    private TextView mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //  We don't need a class field to do most of operations
        Button loginButton = (Button) findViewById(R.id.login_button);
        //  Register a callback to notify us when the login button has been clicked
        loginButton.setOnClickListener(this);

        //  Store the Username and Password text views in class fields as we'll need to retreieve
        //  the text inside them later on
        mUsername = (TextView) findViewById(R.id.username);
        mPassword = (TextView) findViewById(R.id.password);

        //  Login screen should have no action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //  Check if user already logged in
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(Contract.Preferences.LOGGED_IN, false)) {
            //  Go directly to profile screen
            goToProfileScreen(preferences.getString(Contract.Preferences.USERNAME, null));
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() called with: v = [" + v + "]");
        switch (v.getId()) {
            case R.id.login_button:
                //  When the user tapped the button, retrieve the username and password and perform the login
                performLogin(mUsername.getText().toString(), mPassword.getText().toString());
                break;
        }
    }

    private void performLogin(final String username, String password) {
        Call<LoginData> callable = GitHub.Service.Get()
                .checkAuth(Credentials.basic(username, password));

        callable.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                if (response.isSuccessful()) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    preferences.edit()
                            .putBoolean(Contract.Preferences.LOGGED_IN, true)
                            .putString(Contract.Preferences.USERNAME, username)
                            .apply();

                    goToProfileScreen(username);
                } else {
                    switch (response.code()) {
                        case 403:
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToProfileScreen(String username) {
        Intent intent = new Intent(this, ProfileActivity.class);
        //  Now we can send some extra information to the Profile screen
        intent.putExtra(Contract.ProfileActivity.USERNAME, username);
        startActivity(intent);
        //  We no longer need the Login screen
        finish();
    }
}
