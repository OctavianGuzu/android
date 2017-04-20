package ro.softvision.androidworkshop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.softvision.androidworkshop.model.GitHubService;
import ro.softvision.androidworkshop.model.Repository;

public class RepositoriesActivity extends AppCompatActivity {

    private Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        //  We need a reference to our RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //  The Layout Manager is required, we use the vertical linear one
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //  We also need an adapter to bind our views to the model
        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);
        //  Finally fetch the repositories
        fetchRepositories();
    }

    private void fetchRepositories() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Call<List<Repository>> repositoriesCall =
                GitHubService.Service.Get().getUserRepositories(preferences.getString(Contract.Preferences.AUTH_HASH, null),
                        Contract.RepositoryActivity.AFFILIATION);

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

    private static class Adapter extends RecyclerView.Adapter {
        List<Repository> mData;

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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //  The repository must be bound to the view here
            ((ViewHolder) holder).bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mWatcherCount;
            private final TextView mNameAndOwner;
            private final TextView mDescription;
            private final LinearLayout mTopics;
            private final CheckBox mIsPublic;

            /**
             * Create the ViewHolder that holds the references to our views
             * @param itemView The parent view, or root view from the inflated XML
             */
            ViewHolder(View itemView) {
                super(itemView);

                //  Cache all the views we will need when binding the model
                mWatcherCount = (TextView) itemView.findViewById(R.id.watcher_count);
                mNameAndOwner = (TextView) itemView.findViewById(R.id.name_owner);
                mDescription = (TextView) itemView.findViewById(R.id.description);
                mTopics = (LinearLayout) itemView.findViewById(R.id.topics);
                mIsPublic = (CheckBox) itemView.findViewById(R.id.is_public);
            }

            void bind(Repository repository) {
                //  The views are cached, just set the data
                mWatcherCount.setText(String.valueOf(repository.getWatchersCount()));
                mNameAndOwner.setText(itemView.getContext().getString(R.string.repo_name_owner,
                        repository.getName(), repository.getOwner().getLogin()));
                mDescription.setText(repository.getDescription());
                mIsPublic.setChecked(!repository.getPrivate());

                //  TODO: make this pretty when we get to Custom/Compound Views
                mTopics.removeAllViews();
                if (repository.getTopics() != null) {
                    for (String topic : repository.getTopics()) {
                        TextView topicTV = new TextView(itemView.getContext());
                        topicTV.setText(topic);
                        topicTV.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
                        mTopics.addView(topicTV);
                    }
                }
            }
        }
    }
}
