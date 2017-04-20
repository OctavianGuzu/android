package ro.softvision.androidworkshop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {

    public static void LogOut(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        preferences.edit().remove(Contract.Preferences.AUTH_HASH).apply();

        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }
}
