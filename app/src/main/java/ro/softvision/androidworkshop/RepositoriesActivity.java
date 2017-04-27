package ro.softvision.androidworkshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.softvision.androidworkshop.model.GitHubService;
import ro.softvision.androidworkshop.model.Repository;

public class RepositoriesActivity extends AppCompatActivity {
    private static final int CODE_SETTINGS = 0;

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
                    intent.putExtra(Contract.RepositoryDetails.DESCRIPTION, repository.getDescription());
                    intent.putExtra(Contract.RepositoryDetails.IS_PUBLIC, !repository.getPrivate());
                    intent.putExtra(Contract.RepositoryDetails.URL, repository.getUrl());
                    intent.putExtra(Contract.RepositoryDetails.HTML_URL, repository.getHtmlUrl());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
        //  Finally fetch the repositories
        fetchRepositories();
    }

    private void fetchRepositories() {
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

        repositoriesCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                if (response.isSuccessful()) {
                    List<Repository> repositories = response.body();
                    updateUI(repositories);
                } else {
                    Toast.makeText(RepositoriesActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                    Utils.LogOut(RepositoriesActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RepositoriesActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<Repository> repositories) {
        mAdapter.setData(repositories);
        mAdapter.notifyDataSetChanged();
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
                    fetchRepositories();
                    break;
            }
        }
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
