package ro.softvision.androidworkshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class Utils {

    public static void LogOut(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        preferences.edit().remove(Contract.Preferences.AUTH_HASH).apply();

        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public static Dialog ShowLogOutDialog(Activity activity) {
        Dialog dialog = Dialog.NewInstance(
                activity.getString(R.string.ok),
                activity.getString(R.string.cancel),
                activity.getString(R.string.message_logout),
                null
        );
        dialog.show(activity.getFragmentManager(), "logout");
        return dialog;
    }

    public static Intent GetSettingsIntent(Context context, PreferencesActivity.Type type) {
        Intent intent = new Intent(context, PreferencesActivity.class);
        intent.putExtra(Contract.Preferences.PREFERENCE_SCREEN_TYPE, type.ordinal());
        return intent;
    }

    public static String SetToRetrofitQueryString(Set<String> set) {
        String toString = "";
        if (set != null && set.size() > 0) {
            for (String preference : set) {
                toString += preference + ",";
            }
            return toString.substring(0, toString.length() - 1);
        }
        return toString;
    }

    public static void LogOut(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().remove(Contract.Preferences.AUTH_HASH).apply();
    }
}
