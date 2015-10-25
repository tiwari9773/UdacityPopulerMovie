package in.udacity.learning.populermovie.app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

import in.udacity.learning.dbhelper.DBHelper;
import in.udacity.learning.dbhelper.MovieContract;

/**
 * Created by Lokesh on 25-10-2015.
 */
public class TestDatabase extends AndroidTestCase {

    public static final String TAG = TestDatabase.class.getName();

    /*before starting lets clear database*/
    public void deleteDatabase() {
        mContext.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    /* Test Creation of database*/
    public void testCreateDatabase() {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.FavouriteMovie.TABLE_NAME);

        //clean database
        mContext.deleteDatabase(DBHelper.DATABASE_NAME);

        SQLiteDatabase db = new DBHelper(mContext).getWritableDatabase();
        assertEquals("Error: Database Not Open", db.isOpen(), true);

        Cursor c = db.rawQuery("SELECT name From sqlite_master WHERE type='table'", null);
        assertTrue("Error: Database Table not created", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.FavouriteMovie.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> favColumnHashSet = new HashSet<String>();
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_ORIGINAL_LANGUAGE);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_ORIGINAL_TITLE);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_OVERVIEW);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_RELEASE_DATE);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_POSTER_PATH);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_POPULARITY);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_TITLE);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_VIDEO);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_VOTE_AVERAGE);
        favColumnHashSet.add(MovieContract.FavouriteMovie.COL_VOTE_COUNT);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favColumnHashSet.remove(columnName);
            Log.i(TAG, "testCreateDb " + columnName);
        } while (c.moveToNext());


        // if this fails, it means that your table doesn't contain correct columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                tableNameHashSet.isEmpty());

        db.close();
    }
}

