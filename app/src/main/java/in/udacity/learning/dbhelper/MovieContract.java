package in.udacity.learning.dbhelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.DatabaseErrorHandler;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lokesh on 25-10-2015.
 */
public class MovieContract {

    public static String TAG = MovieContract.class.getName();

    /* It should be unique so package name is best idea to give this
    *
    * The "Content authority" is a name for the entire content provider, similar to the
    * relationship between a domain name and its website.  A convenient string to use for the
    * content authority is the package name for the app, which is guaranteed to be unique on the
    * device.
    * */
    public static final String CONTENT_AUTHORITY = "in.udacity.learning.populermovie.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*Path of Favourite Movie Data
    * Possible paths (appended to base content URI for possible URI's)
    * For instance, content://in.udacity.learning.populermovie.app/favourite_movie/ is a valid path for
    * looking at weather data.
    * */
    public static final String PATH_FAVOURITE_MOVIE = "favourites";

    public MovieContract() {
    }

    public static abstract class FavouriteMovie implements BaseColumns {
        /*Base url to connect FavouriteMovie for eg:content://in.udacity.learning.populermovie.app/favourite_movie/ */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIE).build();

        public static final String CONTENT_DIR_TYPE = Uri.parse(ContentResolver.CURSOR_DIR_BASE_TYPE).buildUpon().appendPath(CONTENT_AUTHORITY).appendPath(PATH_FAVOURITE_MOVIE).toString();
        public static final String CONTENT_ITEM_TYPE = Uri.parse(ContentResolver.CURSOR_ITEM_BASE_TYPE).buildUpon().appendPath(CONTENT_AUTHORITY).appendPath(PATH_FAVOURITE_MOVIE).toString();

        public static String TABLE_NAME = "favourites";
        public static String COL_MOVIE_SERVER_ID = "movie_server_id";
        public static String COL_ORIGINAL_LANGUAGE = "original_language";
        public static String COL_ORIGINAL_TITLE = "original_title";
        public static String COL_OVERVIEW = "overview";
        public static String COL_RELEASE_DATE = "release_date";
        public static String COL_POSTER_PATH = "poster_path";
        public static String COL_THUMBNAIL_PATH = "poster_path";
        public static String COL_POPULARITY = "popularity";
        public static String COL_TITLE = "title";
        public static String COL_VIDEO = "video";
        public static String COL_VOTE_AVERAGE = "vote_average";
        public static String COL_VOTE_COUNT = "vote_count";

        public static final String SQL_CREATE = T.CREATE_TABLE + TABLE_NAME
                + T.OPEN_BRACE
                + _ID + T.TYPE_INTEGER + T.PRIMARY_KEY + T.AUTO_INCREMENT + T.SEP_COMMA
                + COL_MOVIE_SERVER_ID + T.TYPE_INTEGER + T.NOT_NULL + T.SEP_COMMA
                + COL_RELEASE_DATE + T.TYPE_INTEGER + T.NOT_NULL + T.SEP_COMMA
                + COL_ORIGINAL_LANGUAGE + T.TYPE_TEXT + T.NOT_NULL + T.SEP_COMMA
                + COL_ORIGINAL_TITLE + T.TYPE_TEXT + T.NOT_NULL + T.SEP_COMMA
                + COL_TITLE + T.TYPE_TEXT + T.NOT_NULL + T.SEP_COMMA
                + COL_OVERVIEW + T.TYPE_TEXT + T.NOT_NULL + T.SEP_COMMA
                + COL_POSTER_PATH + T.TYPE_TEXT + T.NOT_NULL + T.SEP_COMMA
                + COL_THUMBNAIL_PATH + T.TYPE_TEXT + T.NOT_NULL + T.SEP_COMMA
                + COL_POPULARITY + T.TYPE_REAL + T.NOT_NULL + T.SEP_COMMA
                + COL_VOTE_AVERAGE + T.TYPE_REAL + T.NOT_NULL + T.SEP_COMMA
                + COL_VOTE_COUNT + T.TYPE_REAL + T.NOT_NULL + T.SEP_COMMA
                + T.UNIQUE + T.OPEN_BRACE + COL_MOVIE_SERVER_ID + T.CLOSE_BRACE + T.ON_CONFLICT_REPLACE
                + T.CLOSE_BRACE + T.SEMICOLON;

        public static final String SQL_DROP = T.DROP_TABLE + TABLE_NAME;

        /*TO selelct movie with Id*/
        public static Uri buildFavouriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
