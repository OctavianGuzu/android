package ro.softvision.androidworkshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.softvision.androidworkshop.model.GitHubService;
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
        findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard(v);
                return false;
            }
        });

        //  Login screen should have no action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //  Check if user already logged in
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString(Contract.Preferences.AUTH_HASH, null) != null) {
            //  Go directly to profile screen
            goToProfileScreen(preferences.getString(Contract.Preferences.USERNAME, null));
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() called with: v = [" + v + "]");
        switch (v.getId()) {
            case R.id.login_button:
                closeKeyboard(v);
                //  When the user tapped the button, retrieve the username and password and perform the login
                performLogin(mUsername.getText().toString(), mPassword.getText().toString());
                break;
        }
    }

    private void performLogin(final String username, String password) {
        final String authHash = Credentials.basic(username, password);

        Call<LoginData> callable = GitHubService.Service.Get()
                .checkAuth(authHash);

        callable.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                if (response.isSuccessful()) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    preferences.edit()
                            .putString(Contract.Preferences.AUTH_HASH, authHash)
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

    private void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
