package in.udacity.learning.populermovie.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import in.udacity.learning.dbhelper.DBHelper;
import in.udacity.learning.dbhelper.MovieContract;

/**
 * Created by Lokesh on 25-10-2015.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void deleteDatabase(Context context) {
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    private static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
     *  Use this to create some default Movie values for your database tests.
     */
    static ContentValues createFavouriteValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID, 1234);
        cv.put(MovieContract.FavouriteMovie.COL_RELEASE_DATE, 1234);
        cv.put(MovieContract.FavouriteMovie.COL_ORIGINAL_LANGUAGE, "test");

        cv.put(MovieContract.FavouriteMovie.COL_ORIGINAL_TITLE, "test");
        cv.put(MovieContract.FavouriteMovie.COL_TITLE, "test");
        cv.put(MovieContract.FavouriteMovie.COL_OVERVIEW, "test");

        cv.put(MovieContract.FavouriteMovie.COL_POSTER_PATH, "test");
        cv.put(MovieContract.FavouriteMovie.COL_THUMBNAIL_PATH, "test");
        cv.put(MovieContract.FavouriteMovie.COL_POPULARITY, 1234);

        cv.put(MovieContract.FavouriteMovie.COL_VOTE_AVERAGE, 12);
        cv.put(MovieContract.FavouriteMovie.COL_VOTE_COUNT, 12);
//        cv.put(MovieContract.FavouriteMovie.COL_VIDEO, "test");


        return cv;
    }
}
