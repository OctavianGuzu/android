package ro.softvision.androidworkshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ro.softvision.androidworkshop.model.GithubProfile;

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
    private GithubProfile mDisplayedProfile;

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

        String username = getIntent().getStringExtra(Contract.ProfileActivity.USERNAME);
        //  TODO: fetch profile based on username
        updateUI(GithubProfile.MockGithubProfile);
    }

    private void updateUI(GithubProfile profile) {
        mDisplayedProfile = profile;
        //  TODO: load an image URL into the ImageView
        mProfilePicture.setImageResource(R.drawable.octocat);
        mName.setText(profile.getName());
        mOrganization.setText(profile.getOrganization());
        mBio.setText(profile.getBio());
        setTextUnderlined(mLocation, profile.getLocation());
        setTextUnderlined(mEmail, profile.getEmail());
        setTextUnderlined(mCreated, profile.getCreated());
        setTextUnderlined(mUpdated, profile.getUpdated());
        setTextUnderlined(mPublicRepos, profile.getNumPublicRepos().toString());
        setTextUnderlined(mPrivateRepos, profile.getNumPrivateRepos().toString());
    }

    /**
     * Method that sets a text into a TextView and underlines it as TextView does not have this
     * functionality by default
     * @param textView The TextView to apply the underlined content on
     * @param text The text to display underlined
     */
    private void setTextUnderlined(TextView textView, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_blog:
                //  TODO: open a screen displaying the Blog URL
                Toast.makeText(this, "Opening Blog screen at URL: " + mDisplayedProfile.getBlogUrl(),
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
