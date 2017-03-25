package ro.softvision.androidworkshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private void performLogin(String username, String password) {
        //  TODO: make a network call and authenticate the user
        if ("password".equals(password)) {
            Toast.makeText(this, "Logging in with " + username + " and " + password + "...", Toast.LENGTH_SHORT).show();
        } else {
            mUsername.setError("Invalid Username");
            mPassword.setError("Invalid Password");
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
        }
    }
}
