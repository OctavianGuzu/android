package ro.softvision.androidworkshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.softvision.androidworkshop.model.GitHubService;
import ro.softvision.androidworkshop.model.Profile;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mProfilePicture;
    private TextView mName;
    private TextView mOrganization;
    private TextView mBio;
    private TextView mLocation;
    private TextView mEmail;
    private TextView mCreated;
    private TextView mUpdated;
    private TextView mPublicRepos;
    private TextView mPrivateRepos;
    private Profile mDisplayedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfilePicture = (ImageView) findViewById(R.id.profile_picture);
        mName = (TextView) findViewById(R.id.name);
        mOrganization = (TextView) findViewById(R.id.organization);
        mBio = (TextView) findViewById(R.id.bio);
        mLocation = (TextView) findViewById(R.id.location);
        mEmail = (TextView) findViewById(R.id.email);
        mCreated = (TextView) findViewById(R.id.created);
        mUpdated = (TextView) findViewById(R.id.updated);
        mPublicRepos = (TextView) findViewById(R.id.public_repos);
        mPrivateRepos = (TextView) findViewById(R.id.private_repos);
        findViewById(R.id.btn_blog).setOnClickListener(this);
        findViewById(R.id.btn_repositories).setOnClickListener(this);

        fetchProfile();
    }

    private void fetchProfile() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Call<Profile> profileCall =
                GitHubService.Service.Get().getUserProfile(preferences.getString(Contract.Preferences.AUTH_HASH, null));

        profileCall.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    Profile profile = response.body();
                    updateUI(profile);
                } else {
                    Toast.makeText(ProfileActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                    Utils.LogOut(ProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ProfileActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Profile profile) {
        mDisplayedProfile = profile;
        //  TODO: load an image URL into the ImageView
//        mProfilePicture.setImageResource(R.drawable.octocat);
        mName.setText(profile.getName());
        mOrganization.setText(profile.getCompany());
        mBio.setText(profile.getBio());
        setTextUnderlined(mLocation, profile.getLocation());
        setTextUnderlined(mEmail, profile.getEmail());
        setTextUnderlined(mCreated, profile.getCreatedAt());
        setTextUnderlined(mUpdated, profile.getUpdatedAt());
        setTextUnderlined(mPublicRepos, profile.getPublicRepos().toString());
        setTextUnderlined(mPrivateRepos, profile.getOwnedPrivateRepos().toString());
    }

    /**
     * Method that sets a text into a TextView and underlines it as TextView does not have this
     * functionality by default
     * @param textView The TextView to apply the underlined content on
     * @param text The text to display underlined
     */
    private void setTextUnderlined(TextView textView, String text) {
        if (!TextUtils.isEmpty(text)) {
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_blog:
                //  TODO: open a screen displaying the Blog URL
                Toast.makeText(this, "Opening Blog screen at URL: " + mDisplayedProfile.getBlog(),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_repositories:
                startActivity(new Intent(this, RepositoriesActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.LogOut(this);
                break;
        }
        return true;
    }
}
