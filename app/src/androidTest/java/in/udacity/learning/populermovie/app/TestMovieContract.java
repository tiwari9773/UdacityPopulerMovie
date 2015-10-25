package in.udacity.learning.populermovie.app;

import android.net.Uri;
import android.test.AndroidTestCase;

import in.udacity.learning.dbhelper.MovieContract;

/**
 * Created by Lokesh on 25-10-2015.
 */
public class TestMovieContract extends AndroidTestCase {

    public static final String TEST_ID = "1";

    public void testBuildFavouriteURI()
    {
        Uri uri = MovieContract.FavouriteMovie.buildFavouriteUri(Long.parseLong(TEST_ID));
        assertNotNull("Error: Uri is Null, Favourite",uri);

        assertEquals("Error : Uri not equal to expected, Last segment", uri.getLastPathSegment(), TEST_ID);
        assertEquals("Error : Uri not equal to expected, Full URI", uri.toString(), "content://in.udacity.learning.populermovie.app/favourites/"+TEST_ID);

    }
}
