package ro.softvision.androidworkshop.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import ro.softvision.androidworkshop.Contract;
import ro.softvision.androidworkshop.Utils;
import ro.softvision.androidworkshop.database.DbContract;
import ro.softvision.androidworkshop.database.GithubContentProvider;
import ro.softvision.androidworkshop.model.GitHubService;
import ro.softvision.androidworkshop.model.Profile;
import ro.softvision.androidworkshop.model.Repository;

public class SyncService extends IntentService {
    public SyncService() {
        super("Sync Service");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            switch (intent.getAction()) {
                case Contract.Sync.ACTION_SYNC_PROFILE:
                    syncProfile(this);
                    break;
                case Contract.Sync.ACTION_SYNC_REPOSITORIES:
                    syncRepositories(this);
                    break;
            }
        }
    }

    private void syncProfile(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Call<Profile> profileCall =
                GitHubService.Service.Get().getUserProfile(preferences.getString(Contract.Preferences.AUTH_HASH, null));

        try {
            Response<Profile> response = profileCall.execute();
            Profile profile = response.body();
            handleNetworkResponse(profile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "An error occurred!", Toast.LENGTH_SHORT).show();
            Utils.LogOut(context);
        }
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
            getContentResolver().insert(GithubContentProvider.PROFILE_URI, values);
        } catch (SQLException ignored) {
            String selection = DbContract.Profile.ID + "=?";
            String[] selectionArgs = new String[] {
                    String.valueOf(profile.getId())
            };
            getContentResolver().update(GithubContentProvider.PROFILE_URI, values, selection, selectionArgs);
        }
    }

    private void syncRepositories(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String affiliation = Contract.RepositoryActivity.AFFILIATION_DEFAULT;
        if (preferences.contains(Contract.Preferences.Repositories.AFFILIATION)) {
            affiliation = Utils.SetToRetrofitQueryString(preferences.getStringSet(Contract.Preferences.Repositories.AFFILIATION, null));
        }
        Call<List<Repository>> repositoriesCall =
                GitHubService.Service.Get().getUserRepositories(
                        preferences.getString(Contract.Preferences.AUTH_HASH, null),
                        affiliation,
                        preferences.getString(Contract.Preferences.Repositories.SORT, Contract.RepositoryActivity.SORT_DEFAULT));

        try {
            Response<List<Repository>> response = repositoriesCall.execute();
            List<Repository> repositories = response.body();
            // Instead of showing data in the UI, we are not first storing the data in local database
            handleNetworkResponse(repositories);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "An error occurred!", Toast.LENGTH_SHORT).show();
            Utils.LogOut(context);
        }
    }

    private void handleNetworkResponse(List<Repository> repositories) {
        // For each repository retrieved from the networking interface
        for (Repository repository : repositories) {
            ContentValues values = new ContentValues();
            values.put(DbContract.Repository.ID, repository.getId());
            values.put(DbContract.Repository.NAME, repository.getName());
            values.put(DbContract.Repository.DESCRIPTION, repository.getDescription());
            values.put(DbContract.Repository.IS_PUBLIC, !repository.getPrivate());
            values.put(DbContract.Repository.DEFAULT_BRANCH, repository.getDefaultBranch());
            values.put(DbContract.Repository.OWNER_ID, repository.getOwner().getId());

            try {
                // Try to add it
                getContentResolver().insert(GithubContentProvider.REPOSITORY_URI, values);
            } catch(SQLException ignored) {
                // If it already exists, update it
                String selection = DbContract.Repository.ID + "=" + repository.getId();
                getContentResolver().update(GithubContentProvider.REPOSITORY_URI, values, selection, null);
            }
        }
    }
}
