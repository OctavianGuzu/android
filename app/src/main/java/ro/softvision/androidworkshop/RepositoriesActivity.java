package ro.softvision.androidworkshop;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.softvision.androidworkshop.database.DbContract;
import ro.softvision.androidworkshop.database.GithubContentProvider;
import ro.softvision.androidworkshop.model.Repository;
import ro.softvision.androidworkshop.services.SyncService;

public class RepositoriesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CODE_SETTINGS = 0;

    private static final int LOADER_REPOSITORIES = R.string.loader_repositories;

    private Adapter mAdapter;
    private boolean mCanShowDetails = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        //  We need a reference to our RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mCanShowDetails = (findViewById(R.id.container) != null);
        //  The Layout Manager is required, we use the vertical linear one
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //  We also need an adapter to bind our views to the model
        mAdapter = new Adapter(new Adapter.Callback() {
            @Override
            public void show(Repository repository) {
                if (mCanShowDetails) {
                    Fragment details = RepositoryDetailsFragment.New(repository);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, details)
                            .commit();
                } else {
                    Intent intent = new Intent(RepositoriesActivity.this, RepositoryDetailsActivity.class);
                    intent.putExtra("repository", repository);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);

        // Populate the list with whatever data with have in the local database (so we don't have
        // an empty screen when fetching repositories from the network). Also, in case of no
        // internet connection, we still have something to show in the UI.
        updateUIFromDb();
        //  Finally attempt to fetch the repositories
        syncRepositories();
    }

    private void syncRepositories() {
        Intent intent = new Intent(this, SyncService.class);
        intent.setAction(Contract.Sync.ACTION_SYNC_REPOSITORIES);
        startService(intent);
    }

    private void updateUIFromDb() {
        // Fetch all of the repositories from the local database asynchronously
        getSupportLoaderManager().initLoader(LOADER_REPOSITORIES, null, this);
    }

    private void updateFromCursor(Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) { // Move to the first position in the cursor
                // Extract all of the column indexes based on the column names
                List<Repository> myRepos = new ArrayList<>();
                int idIndex = cursor.getColumnIndex(DbContract.Repository.ID);
                int nameIndex = cursor.getColumnIndex(DbContract.Repository.NAME);
                int descriptionIndex = cursor.getColumnIndex(DbContract.Repository.DESCRIPTION);
                int isPublicIndex = cursor.getColumnIndex(DbContract.Repository.IS_PUBLIC);

                do {
                    // And extract each repository
                    Repository repository = new Repository();
                    repository.setId(cursor.getInt(idIndex));
                    repository.setName(cursor.getString(nameIndex));
                    repository.setDescription(cursor.getString(descriptionIndex));
                    repository.setPrivate(cursor.getInt(isPublicIndex) == 0);
                    myRepos.add(repository);
                } while (cursor.moveToNext());  // While iterating over the cursor

                // Show the repositories in the UI
                mAdapter.setData(myRepos);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.repositories_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = Utils.GetSettingsIntent(this, PreferencesActivity.Type.Repositories);
                startActivityForResult(intent, CODE_SETTINGS);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_SETTINGS:
                    //  We refresh in case the settings changed
                    syncRepositories();
                    break;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_REPOSITORIES:
                return new CursorLoader(this, GithubContentProvider.REPOSITORY_URI,
                        null, null, null, null);
        }
        return new CursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_REPOSITORIES:
                updateFromCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static class Adapter extends RecyclerView.Adapter {
        List<Repository> mData;
        Callback mCallback;

        public Adapter(Callback callback) {
            mCallback = callback;
        }

        public interface Callback {
            void show(Repository repository);
        }

        /**
         * Set our repository list that's the model for our views
         * @param data repository list
         */
        public void setData(List<Repository> data) {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //  A new view needs to be inflated here
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_repository, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            //  The repository must be bound to the view here
            ((ViewHolder) holder).bind(mData.get(position), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.show(mData.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mName;

            /**
             * Create the ViewHolder that holds the references to our views
             * @param itemView The parent view, or root view from the inflated XML
             */
            ViewHolder(View itemView) {
                super(itemView);

                //  Cache all the views we will need when binding the model
                mName = (TextView) itemView.findViewById(R.id.name);
            }

            void bind(Repository repository, View.OnClickListener onClickListener) {
                //  The views are cached, just set the data
                mName.setText(repository.getName());
                itemView.setOnClickListener(onClickListener);
            }
        }
    }
}
