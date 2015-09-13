package in.udacity.learning.serviceutility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import in.udacity.learning.keys.ResponceParsingKeys;
import in.udacity.learning.model.MovieItem;

/**
 * Created by Lokesh on 06-09-2015.
 */
public class JSONParser {

    public static List<MovieItem> parseMovieList(String jSonString)
    {
        List<MovieItem> lsMovie = new ArrayList();

        try {
            JSONObject jsonObject = new JSONObject(jSonString);
            JSONArray jsonArray = jsonObject.getJSONArray(ResponceParsingKeys.MovieKeys.RESULTS);

            //set Time
            Calendar dayTime = new GregorianCalendar();
            Date date =  new Date();
            dayTime.setTime(date);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject origArray = jsonArray.getJSONObject(i);

                String title = origArray.getString(ResponceParsingKeys.MovieKeys.TITLE);
                String path = origArray.getString(ResponceParsingKeys.MovieKeys.POSTER_PATH);

                MovieItem temp = new MovieItem(title,path);
                lsMovie.add(temp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lsMovie;
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
            * so for convenience we're breaking it out into its own method now.
            */
    private static String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private static String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

}
