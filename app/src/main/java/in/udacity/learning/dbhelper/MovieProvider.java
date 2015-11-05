package in.udacity.learning.dbhelper;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Lokesh on 25-10-2015.
 */
public class MovieProvider extends ContentProvider {

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    private DBHelper mOpenHelper;

    /*Constant which identifies query*/
    public static final int FAVOURITE = 1;
    public static final int FAVOURITE_BY_ID = 2;
    public static final int FAVOURITE_BY_SERVER_ID = 3;
    public static final int FAVOURITE_IMAGE = 3;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String content_authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(content_authority, MovieContract.PATH_FAVOURITE_MOVIE, FAVOURITE);
        uriMatcher.addURI(content_authority, MovieContract.PATH_FAVOURITE_MOVIE + "/*", FAVOURITE_BY_ID);
        uriMatcher.addURI(content_authority, MovieContract.PATH_FAVOURITE_MOVIE + "/"+MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID+"/*", FAVOURITE_BY_ID);

        return uriMatcher;
    }

    public static final String sFavouriteWithId = MovieContract.FavouriteMovie._ID + "= ?";
    public static final String sFavouriteWithServerId = MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID + "= ?";

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor resCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVOURITE:
                resCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteMovie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVOURITE_BY_ID:
                String getId = uri.getLastPathSegment();
                resCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteMovie.TABLE_NAME,
                        projection,
                        sFavouriteWithId,
                        new String[]{getId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVOURITE_BY_SERVER_ID:
                String getServerId = uri.getLastPathSegment();
                resCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteMovie.TABLE_NAME,
                        projection,
                        sFavouriteWithServerId,
                        new String[]{getServerId},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        resCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVOURITE:
                return MovieContract.FavouriteMovie.CONTENT_DIR_TYPE;

            case FAVOURITE_BY_ID:
                return MovieContract.FavouriteMovie.CONTENT_ITEM_TYPE;

            case FAVOURITE_BY_SERVER_ID:
                return MovieContract.FavouriteMovie.CONTENT_ITEM_TYPE;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVOURITE:
                long _id = db.insert(MovieContract.FavouriteMovie.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavouriteMovie.buildFavouriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {

            case FAVOURITE:
                rowsDeleted = db.delete(MovieContract.FavouriteMovie.TABLE_NAME, null, null);
                break;

            case FAVOURITE_BY_ID:
                rowsDeleted = db.delete(MovieContract.FavouriteMovie.TABLE_NAME, sFavouriteWithId, new String[]{uri.getLastPathSegment()});
                break;

            case FAVOURITE_BY_SERVER_ID:
                rowsDeleted = db.delete(MovieContract.FavouriteMovie.TABLE_NAME, sFavouriteWithServerId, new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowAffected = 0;

        switch (match) {
            case FAVOURITE_BY_ID:
                //rowAffected = db.update();
                break;

            case FAVOURITE_BY_SERVER_ID:
                //rowAffected = db.delete();
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowAffected;
    }

    // specifically to assist the testing framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
