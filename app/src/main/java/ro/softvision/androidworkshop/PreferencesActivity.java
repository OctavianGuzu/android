package ro.softvision.androidworkshop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class PreferencesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public enum Type { Nothing, Repositories }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Type type;
        if (getIntent() != null) {
            type = Type.values()[getIntent().getIntExtra(Contract.Preferences.PREFERENCE_SCREEN_TYPE, 0)];
        } else {
            type = Type.Nothing;
        }

        switch (type) {
            case Repositories: {
                showFragment(new RepositoriesPreferenceFragment());
                break;
            }
            case Nothing:
            default: {
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  We will check if the user actually changed something
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showFragment(PreferenceFragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //  The user changed something, signal that to the previous activity by changing the result
        setResult(RESULT_OK);
    }

    public static class RepositoriesPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences_repositories);
        }
    }
}
