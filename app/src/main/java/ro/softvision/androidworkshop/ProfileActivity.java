package ro.softvision.androidworkshop;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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
import ro.softvision.androidworkshop.database.DbContract;
import ro.softvision.androidworkshop.database.MySqlHelper;
import ro.softvision.androidworkshop.model.GitHubService;
import ro.softvision.androidworkshop.model.Profile;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, Dialog.Callbacks {

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
    private Dialog mLogoutDialog;

    private SQLiteDatabase mDbConnection;

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

        // Establish the link to the database
        MySqlHelper mySqlHelper = new MySqlHelper(this);
        mDbConnection = mySqlHelper.getWritableDatabase();

        // Populate the UI with whatever data with have in the local database (so we don't have
        // an empty screen when fetching the profile from the network). Also, in case of no
        // internet connection, we still have something to show in the UI.
        updateUIFromDb();
        //  Finally attempt to fetch the repositories
        fetchProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to close the link to the database
        if (mDbConnection != null) {
            mDbConnection.close();
        }
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
                    handleNetworkResponse(profile);
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

    private void handleNetworkResponse(Profile profile) {
        // Serialize the profile in a DB-relatable format
        ContentValues values = new ContentValues();
        values.put(DbContract.Profile.ID, profile.getId());
        values.put(DbContract.Profile.LOGIN, profile.getLogin());
        values.put(DbContract.Profile.NAME, profile.getName());
        values.put(DbContract.Profile.COMPANY, profile.getCompany());
        values.put(DbContract.Profile.AVATAR_URL, profile.getAvatarUrl());
        values.put(DbContract.Profile.BIO, profile.getBio());
        values.put(DbContract.Profile.EMAIL, profile.getEmail());
        values.put(DbContract.Profile.LOCATION, profile.getLocation());
        values.put(DbContract.Profile.CREATED_AT, profile.getCreatedAt());
        values.put(DbContract.Profile.UPDATED_AT, profile.getUpdatedAt());
        values.put(DbContract.Profile.PUBLIC_REPOS, profile.getPublicRepos());
        values.put(DbContract.Profile.OWNED_PRIVATE_REPOS, profile.getOwnedPrivateRepos());

        try {
            mDbConnection.insertOrThrow(DbContract.Profile.TABLE, null, values);
        } catch (SQLException ignored) {
            String selection = DbContract.Profile.ID + "=?";
            String[] selectionArgs = new String[] {
                    String.valueOf(profile.getId())
            };
            mDbConnection.update(DbContract.Profile.TABLE, values, selection, selectionArgs);
        }

        updateUIFromDb();
    }


    private void updateUIFromDb() {
        // Fetch all of the repositories from the local database
        Cursor cursor = mDbConnection.query(DbContract.Profile.TABLE, null, null, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) { // Move to the first position in the cursor
                // Extract all of the column indexes based on the column names
                int idIndex = cursor.getColumnIndex(DbContract.Profile.ID);
                int loginIndex = cursor.getColumnIndex(DbContract.Profile.LOGIN);
                int nameIndex = cursor.getColumnIndex(DbContract.Profile.NAME);
                int companyIndex = cursor.getColumnIndex(DbContract.Profile.COMPANY);
                int avatarIndex = cursor.getColumnIndex(DbContract.Profile.AVATAR_URL);
                int bioIndex = cursor.getColumnIndex(DbContract.Profile.BIO);
                int emailIndex = cursor.getColumnIndex(DbContract.Profile.EMAIL);
                int locationIndex = cursor.getColumnIndex(DbContract.Profile.LOCATION);
                int createdAtIndex = cursor.getColumnIndex(DbContract.Profile.CREATED_AT);
                int updatedAtIndex = cursor.getColumnIndex(DbContract.Profile.UPDATED_AT);
                int publicReposIndex = cursor.getColumnIndex(DbContract.Profile.PUBLIC_REPOS);
                int ownedPrivateReposIndex = cursor.getColumnIndex(DbContract.Profile.OWNED_PRIVATE_REPOS);

                // And extract the data for the profile
                Profile profile = new Profile();
                profile.setId(cursor.getInt(idIndex));
                profile.setLogin(cursor.getString(loginIndex));
                profile.setName(cursor.getString(nameIndex));
                profile.setCompany(cursor.getString(companyIndex));
                profile.setAvatarUrl(cursor.getString(avatarIndex));
                profile.setBio(cursor.getString(bioIndex));
                profile.setEmail(cursor.getString(emailIndex));
                profile.setLocation(cursor.getString(locationIndex));
                profile.setCreatedAt(cursor.getString(createdAtIndex));
                profile.setUpdatedAt(cursor.getString(updatedAtIndex));
                profile.setPublicRepos(cursor.getInt(publicReposIndex));
                profile.setOwnedPrivateRepos(cursor.getInt(ownedPrivateReposIndex));

                // And show it in the UI
                updateUI(profile);
            }
            // Don't forget to free the cursor
            cursor.close();
        }
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
                mLogoutDialog = Utils.ShowLogOutDialog(this);
                break;
        }
        return true;
    }

    @Override
    public void onDialogPositiveClick(Dialog dialog) {
        //  Identify which dialog was clicked
        if (dialog == mLogoutDialog) {
            // Clean up the local database (if another user will be logging in, we don't want the
            // previous user's info to be available for him)
            mDbConnection.delete(DbContract.Profile.TABLE, null, null);
            mDbConnection.delete(DbContract.Repository.TABLE, null, null);

            // Log the user out from the UI
            Utils.LogOut(this);
        }
    }

    @Override
    public void onDialogNegativeClick(Dialog dialog) {
        //  Do nothing
    }
}
