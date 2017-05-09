package ro.softvision.androidworkshop.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * A content provider exposing the local database. The following items are exposed:
 *   * All repositories
 *   * A single repository identified by its ID
 *   * All profiles
 *   * A single profile identified by its ID
 */
public class GithubContentProvider extends ContentProvider {
    /**
     * The link to the local database
     */
    private MySqlHelper mySqlHelper;

    /**
     * The entities we are matching URIs towards
     */
    interface Entities {
        int REPOSITORIES = 0;
        int REPOSITORY = 1;
        int PROFILES = 2;
        int PROFILE = 3;
    }

    /**
     * The authority of the provider as specified in the manifest file. Usually, an authority is
     * created based on the application's package name and appending '.provider' at the end.
     */
    private static final String AUTHORITY = "ro.softvision.androidworkshop.provider";

    /**
     * The base URI based on which we are exposing content. Identifying entities will be done by
     * constructing an URI based off of the CONTENT_URI
     */
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * The list of per entity URIs
     */
    public static final Uri REPOSITORY_URI = CONTENT_URI.buildUpon()
            .appendPath("repositories")
            .build();
    public static final Uri PROFILE_URI = CONTENT_URI.buildUpon()
            .appendPath("profile")
            .build();

    /**
     * The matcher that binds URIs to Entity IDs
     */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * The rules for matching exposed entities to URIs
     */
    static {
        // Exposing all repositories
        sUriMatcher.addURI(AUTHORITY, "repositories", Entities.REPOSITORIES);
        // Exposing a single repository
        sUriMatcher.addURI(AUTHORITY, "repositories/#", Entities.REPOSITORY);
        // Exposing all profiles
        sUriMatcher.addURI(AUTHORITY, "profile", Entities.PROFILES);
        // Exposing a single profile
        sUriMatcher.addURI(AUTHORITY, "profile/#", Entities.PROFILE);
    }

    /**
     * Required empty constructor
     */
    public GithubContentProvider() {
    }

    /**
     * Callback to set up the provider when it is first requested (throuhg an operation)
     * @return whether it was set up correctly or not
     */
    @Override
    public boolean onCreate() {
        // Create the link towards the database
        mySqlHelper = new MySqlHelper(getContext());
        return true;
    }

    /**
     * Helper method that uses our URI matcher to determine the mimetype of the data we're
     * requesting. Each content entity mime type is built as follows:
     *   1. the type is vendor specific: 'vnd.android.cursor.item/dir'. 'item' or 'dir' is selected
     *   based on the requested data (single or multiple rows)
     *   2. the subtype is application specific: usually the full class name where the first
     *   segment of the package name is replaced with 'vnd'
     * @param uri the URI of the content we are operating on
     * @return the mimetype of the data based on the requested URI
     */
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORIES:
                return "vnd.android.cursor.dir/vnd.softvision.androidworkshop.repository";
            case Entities.REPOSITORY:
                return "vnd.android.cursor.item/vnd.softvision.androidworkshop.repository";
            case Entities.PROFILES:
                return "vnd.android.cursor.dir/vnd.softvision.androidworkshop.profile";
            case Entities.PROFILE:
                return "vnd.android.cursor.item/vnd.softvision.androidworkshop.profile";
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Implements the basic CREATE operation, inserting the values into the local database
     * @param uri the request content URI; we will be grouping URIs seeing as we cannot operate
     *            on multiple rows at the same time
     * @param values the values that need to be added
     * @return the URI of the newly added data
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
        long id;
        Uri modifiedUri = GithubContentProvider.CONTENT_URI;

        // Group multi/single operations to a single category
        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORIES:
            case Entities.REPOSITORY:
                // Insert the data
                id = database.insertOrThrow(DbContract.Repository.TABLE, null, values);
                // Build the specified entity
                modifiedUri = modifiedUri.buildUpon()
                        .appendPath(DbContract.Repository.TABLE)
                        .build();
                break;
            case Entities.PROFILES:
            case Entities.PROFILE:
                // Insert the data
                id = database.insertOrThrow(DbContract.Profile.TABLE, null, values);
                // Build the specified entity
                modifiedUri = modifiedUri.buildUpon()
                        .appendPath(DbContract.Profile.TABLE)
                        .build();
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        // If the operation was successful
        if (id >= 0) {
            // Construct the corresponding URI for this newly added item
            modifiedUri = ContentUris.withAppendedId(modifiedUri, id);
            // Notify the listeners that data has changed
            getContext().getContentResolver().notifyChange(modifiedUri, null);
            return modifiedUri;
        }
        return null;
    }

    /**
     * Implements the basic READ operation from the local database
     * @param uri the requested content URI; we will be using the entire URI matching scheme for
     *            allowing single/multi-line queries
     * @param projection the projection of the query (optional)
     * @param selection the selection of the query (optional)
     * @param selectionArgs the selection arguments of the query (optional)
     * @param sortOrder the sort order of the query (optional)
     * @return a Cursor containing the requested entities
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Sanitize the selection
        if (TextUtils.isEmpty(selection)) {
            selection = "1";
        }

        Cursor cursor = null;

        // Use the URI matcher to determine the entity and operation type (single/multi-line)
        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORIES: {
                // Requesting all repositories
                cursor = mySqlHelper.getReadableDatabase().query(DbContract.Repository.TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case Entities.REPOSITORY: {
                // Building upon the selection to just choose the corresponding repository
                String sqlSelection = " AND " + DbContract.Repository.ID + "=" + uri.getLastPathSegment();
                cursor = mySqlHelper.getReadableDatabase().query(DbContract.Repository.TABLE,
                        projection, selection + sqlSelection, selectionArgs, null, null, sortOrder);
                break;
            }
            case Entities.PROFILES: {
                // Requesting all profiles
                cursor = mySqlHelper.getReadableDatabase().query(DbContract.Profile.TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case Entities.PROFILE: {
                // Building upon the selection to just choose the corresponding profile
                String sqlSelection = " AND " + DbContract.Profile.ID + "=" + uri.getLastPathSegment();
                cursor = mySqlHelper.getReadableDatabase().query(DbContract.Profile.TABLE,
                        projection, (TextUtils.isEmpty(selection) ? "1" : selection)
                                + sqlSelection, selectionArgs, null, null, sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    /**
     * Inplements the basic UPDATE operation on the local the database
     * @param uri the requested content URI; we will be using the entire URI matching scheme for
     *            allowing single/multi-line queries
     * @param values the columns we are updating
     * @param selection the selection of the operation (optional)
     * @param selectionArgs the selection arguments of the operation (optional)
     * @return the number of rows affected by the operation
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updatedCount;
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();

        // Sanitize the selection
        if (TextUtils.isEmpty(selection)) {
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORY:
                // If a single row was request, update the selection to reflect that
                selection += " AND " + DbContract.Repository.ID + "=" + uri.getLastPathSegment();
            case Entities.REPOSITORIES:
                // Update all rows matching the selection
                updatedCount = database.update(DbContract.Repository.TABLE, values, selection, selectionArgs);
                break;
            case Entities.PROFILE:
                // If a single row was request, update the selection to reflect that
                selection += " AND " + DbContract.Repository.ID + "=" + uri.getLastPathSegment();
            case Entities.PROFILES:
                // Update all rows matching the selection
                updatedCount = database.update(DbContract.Profile.TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        // Notify listeners that this URI has been modified
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedCount;
    }

    /**
     * The means towards deleting an item/multiple items.
     * @param uri the requested URI
     * @param selection the selection
     * @param selectionArgs the selection arguments
     * @return the number of rows affected by the operation
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
        int rowsAffected;

        // Sanitize the selection
        if (TextUtils.isEmpty(selection)) {
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORY:
                // If a single row was request, update the selection to reflect that
                selection += " AND " + DbContract.Repository.ID + "=" + uri.getLastPathSegment();
            case Entities.REPOSITORIES:
                // Delete all rows matching the selection
                rowsAffected = database.delete(DbContract.Repository.TABLE, selection, selectionArgs);
                break;
            case Entities.PROFILE:
                // If a single row was request, update the selection to reflect that
                selection += " AND " + DbContract.Profile.ID + "=" + uri.getLastPathSegment();
            case Entities.PROFILES:
                // Delete all rows matching the selection
                rowsAffected = database.delete(DbContract.Profile.TABLE, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        // Notify listeners that this URI has been modified
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }
}
