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

        Repository repository = getIntent().getParcelableExtra("repository");

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, RepositoryDetailsFragment.New(repository))
                    .commit();
        }
    }
}
