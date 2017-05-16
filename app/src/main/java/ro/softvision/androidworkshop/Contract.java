package ro.softvision.androidworkshop;

public interface Contract {
    interface Preferences {
        String PREFERENCE_SCREEN_TYPE = "preference_screen_type";
        String AUTH_HASH = "auth_hash";
        String USERNAME = ProfileActivity.USERNAME;

        interface Repositories {
            String AFFILIATION = "affiliation";
            String SORT = "sort";
        }
    }

    interface ProfileActivity {
        String USERNAME = "username";
    }

    interface RepositoryActivity {
        String AFFILIATION_DEFAULT = "owner,collaborator,organization_member";
        String SORT_DEFAULT = "full_name";
    }

    interface RepositoryDetails {
        String DESCRIPTION = "description";
        String IS_PUBLIC = "is_public";
        String URL = "url";
        String HTML_URL = "html_url";
    }

    interface Dialog {
        String POSITIVE_BUTTON = "positive_button";
        String NEGATIVE_BUTTON = "negative_button";
        String MESSAGE = "message";
        String TITLE = "title";
    }

    interface Sync {
        String ACTION_SYNC_PROFILE = "ro.softvision.sync_profile";
        String ACTION_SYNC_REPOSITORIES = "ro.softvision.sync_repositories";
    }
}
