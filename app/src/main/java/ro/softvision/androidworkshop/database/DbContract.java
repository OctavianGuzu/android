package ro.softvision.androidworkshop.database;

import android.provider.BaseColumns;

/**
 * Created by radu on 4/27/17.
 *
 * The Contract exposing the data in the local database: each entity will have it's own interface
 * which provides the following details:
 *   1. table name
 *   2. column names
 */
public interface DbContract {

    interface Profile extends BaseColumns {
        /**
         * The SQLite table name for this entity
         */
        String TABLE                = "profile";

        /**
         * The SQLite table columns along with _id exposted by BaseColumns
         */
        String ID                   = "id";
        String LOGIN                = "login";
        String NAME                 = "name";
        String COMPANY              = "company";
        String AVATAR_URL           = "avatar_url";
        String BIO                  = "bio";
        String EMAIL                = "email";
        String LOCATION             = "location";
        String CREATED_AT           = "created_at";
        String UPDATED_AT           = "updated_at";
        String PUBLIC_REPOS         = "public_repos";
        String OWNED_PRIVATE_REPOS  = "owned_private_repos";
    }

    interface Repository extends BaseColumns {
        /**
         * The SQLite table name for this entity
         */
        String TABLE            = "repositories";

        /**
         * The SQLite table columns along with _id exposted by BaseColumns
         */
        String ID               = "id";
        String NAME             = "name";
        String DESCRIPTION      = "description";
        String IS_PUBLIC        = "is_public";
        String DEFAULT_BRANCH   = "default_branch";
        String OWNER_ID         = "owner_id";
    }
}
