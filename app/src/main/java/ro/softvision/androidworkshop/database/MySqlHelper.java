package ro.softvision.androidworkshop.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by radu on 4/27/17.
 *
 * This represents the link between the application and the database support. It implements
 * the following functionality:
 *   1. creating the SQLite tables
 *   2. handling database upgrades
 *
 * An SQLiteOpenHelper can also override functionality when opening a connection towards the
 * local database, as well as for handling downgrades.
 */
public class MySqlHelper extends SQLiteOpenHelper {
    /**
     * The name of the SQLite persistence support. If you're using an emulator (or have root
     * support on your device), you can actually pull the database file from the device and on
     * your workstation using the following command:
     *
     *   $ adb pull /data/data/<your-application-package>/database/github.db
     */
    private static final String DB_NAME = "github.db";

    /**
     * The current version of the database. Since we don't have upgrade support, this will remain
     * unchanged for now.
     */
    private static final int DB_VERSION = 1;

    public MySqlHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the profile table (this is where all of the profile data will be persisted)
        db.execSQL("CREATE TABLE " + DbContract.Profile.TABLE + "("
                + DbContract.Profile._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + DbContract.Profile.ID + " INTEGER UNIQUE ON CONFLICT ABORT, "
                + DbContract.Profile.LOGIN + " TEXT, "
                + DbContract.Profile.NAME + " TEXT, "
                + DbContract.Profile.COMPANY + " TEXT, "
                + DbContract.Profile.AVATAR_URL + " TEXT, "
                + DbContract.Profile.BIO + " TEXT, "
                + DbContract.Profile.EMAIL + " TEXT, "
                + DbContract.Profile.LOCATION + " TEXT, "
                + DbContract.Profile.CREATED_AT + " TEXT, "
                + DbContract.Profile.UPDATED_AT + " TEXT, "
                + DbContract.Profile.PUBLIC_REPOS + " INTEGER, "
                + DbContract.Profile.OWNED_PRIVATE_REPOS + " INTEGER"
                + ")");

        // Create the repository table (this is where all of the repository data will be persisted)
        db.execSQL("CREATE TABLE " + DbContract.Repository.TABLE + "("
            + DbContract.Repository._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + DbContract.Repository.ID + " INTEGER UNIQUE ON CONFLICT ABORT, "
                + DbContract.Repository.NAME + " TEXT, "
                + DbContract.Repository.DESCRIPTION + " TEXT, "
                + DbContract.Repository.IS_PUBLIC + " BOOLEAN, "
                + DbContract.Repository.DEFAULT_BRANCH + " TEXT, "
                + DbContract.Repository.OWNER_ID + " INTEGER"
            + ")");

        // Notice how the GitHub IDs have a UNIQUE clause which leads to aborting on conflict. This
        // is used to prevent accidentally inserting the same entity twice and throwing an exception
        // in case this were to happen.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We are currently not supporting database upgrades, so just clean it up and re-create it
        db.execSQL("DROP TABLE " + DbContract.Repository.TABLE);
        onCreate(db);
    }
}
