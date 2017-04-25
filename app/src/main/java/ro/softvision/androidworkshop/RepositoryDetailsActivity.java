package ro.softvision.androidworkshop;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ro.softvision.androidworkshop.model.Repository;

public class RepositoryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null) {
            Toast.makeText(this, "How in the world did you start me???", Toast.LENGTH_SHORT).show();
            return ;
        }

        Repository repository = new Repository();

        repository.setDescription(getIntent().getStringExtra(Contract.RepositoryDetails.DESCRIPTION));
        repository.setPrivate(!getIntent().getBooleanExtra(Contract.RepositoryDetails.IS_PUBLIC, true));
        repository.setUrl(getIntent().getStringExtra(Contract.RepositoryDetails.URL));
        repository.setHtmlUrl(getIntent().getStringExtra(Contract.RepositoryDetails.HTML_URL));

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, RepositoryDetailsFragment.New(repository))
                    .commit();
        }
    }
}
