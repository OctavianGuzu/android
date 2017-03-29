package ro.softvision.androidworkshop;

public interface Contract {
    interface Preferences {
        String LOGGED_IN = "logged_in";
        String USERNAME = ProfileActivity.USERNAME;
    }
    interface ProfileActivity {
        String USERNAME = "username";
    }
}
