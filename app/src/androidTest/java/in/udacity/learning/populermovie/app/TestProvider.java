package in.udacity.learning.populermovie.app;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

import in.udacity.learning.dbhelper.DBHelper;
import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.dbhelper.MovieProvider;

/**
 * Created by Lokesh on 25-10-2015.
 */
public class TestProvider extends AndroidTestCase {
    public static final String TAG = "TestProvider";

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //clean database
        deleteAllRecords();
    }


    /*
      This helper function deletes all records from both database tables using the ContentProvider.
      It also queries the ContentProvider to make sure that the database has been successfully
      deleted, so it cannot be used until the Query and Delete functions have been written
      in the ContentProvider.

      Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
      the delete functionality in the ContentProvider.
    */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.FavouriteMovie.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.FavouriteMovie.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();
    }


    /*
     * Refactor this function to use the deleteAllRecordsFromProvider functionality
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
       It verifies that the ContentProvider returns
       the correct type for each type of URI that it can handle.

    */
    public void testGetType() {
        // vnd.android.cursor.dir/in.udacity.learning.populermovie.app/favourite
        String type = mContext.getContentResolver().getType(MovieContract.FavouriteMovie.CONTENT_URI);

        // vnd.android.cursor.dir/in.udacity.learning.populermovie.app/favourite
        assertEquals("Error: the MovieFavourite CONTENT_URI should return MovieFavourite.CONTENT_DIR_TYPE",
                MovieContract.FavouriteMovie.CONTENT_DIR_TYPE, type);

        long id = 1;
        type = mContext.getContentResolver().getType(MovieContract.FavouriteMovie.buildFavouriteUri(id));

        // vnd.android.cursor.dir/in.udacity.learning.populermovie.app/favourite/1
        assertEquals("Error: the MovieFavourite CONTENT_URI should return MovieFavourite.CONTENT_ITEM_TYPE",
                MovieContract.FavouriteMovie.CONTENT_ITEM_TYPE, type);
    }

    /*Test Value is getting properly inserted or not */
    public int testInsertDatabase() {
        ContentValues cv = TestUtilities.createFavouriteValues();

        //long id = new DBHelper(mContext).insertInTable(MovieContract.FavouriteMovie.TABLE_NAME, null, cv);
           /*OR*/
        //SQLiteDatabase db = new DBHelper(mContext).getWritableDatabase();
        //long id = db.insert(MovieContract.FavouriteMovie.TABLE_NAME, null, cv);
           /*OR*/
        // assertTrue("Not inserted Value in Favourite table" + id, id > 0);

        Uri uri = MovieContract.FavouriteMovie.CONTENT_URI;
        uri = mContext.getContentResolver().insert(uri, cv);
        // vnd.android.cursor.dir/in.udacity.learning.populermovie.app/favourite
        String type = mContext.getContentResolver().getType(uri);
        // vnd.android.cursor.dir/in.udacity.learning.populermovie.app/favourite/1
        assertEquals("Error: the MovieFavourite CONTENT_URI should return MovieFavourite.CONTENT_ITEM_TYPE",
                MovieContract.FavouriteMovie.CONTENT_ITEM_TYPE, type);

        String id = uri.getLastPathSegment();
        assertTrue("Not inserted Value in Favourite table" + id, !id.equals("-1"));

        return Integer.parseInt(id);
    }

    public void testBasicReadQuery() {

        /* Insert New Record*/
        ContentValues cv = TestUtilities.createFavouriteValues();
        testInsertDatabase();

        Cursor cur = mContext.getContentResolver().query(MovieContract.FavouriteMovie.CONTENT_URI, null, null, null, null);
        assertTrue("Cursor is null , Read Test Failed", null != cur);
        assertTrue("Cursor does not contain any value", cur.moveToFirst());

        TestUtilities.validateCursor("Not equal what inserted", cur, cv);
    }

    public void testDeleteQuery() {
        /* Insert New Record*/
        int id = testInsertDatabase();

        Uri uri = MovieContract.FavouriteMovie.buildFavouriteUri(id);
        int recordAffected = mContext.getContentResolver().delete(uri, null, null);
        assertTrue("Nothing is deleted", recordAffected == 1);
    }
}
