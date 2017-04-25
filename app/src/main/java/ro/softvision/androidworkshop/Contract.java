package ro.softvision.androidworkshop;

public interface Contract {
    interface Preferences {
        String AUTH_HASH = "auth_hash";
        String USERNAME = ProfileActivity.USERNAME;
    }

    interface ProfileActivity {
        String USERNAME = "username";
    }

    interface RepositoryActivity {
        String OWNER = "owner";
        String AFFILIATION = OWNER;
    }

    interface RepositoryDetails {
        String DESCRIPTION = "description";
        String IS_PUBLIC = "is_public";
        String URL = "url";
        String HTML_URL = "html_url";
    }
}
