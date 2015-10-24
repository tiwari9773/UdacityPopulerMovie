package in.udacity.learning.web_service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import in.udacity.learning.model.MovieItem;

/**
 * Created by Lokesh on 06-09-2015.
 */
public class JSONParser {

    // give the parsed result of movie list
    public static List<MovieItem> parseMovieList(String jSonString) {
        List<MovieItem> lsMovie = new ArrayList();

        try {
            JSONObject jsonObject = new JSONObject(jSonString);
            String total_pages = jsonObject.getString(WebServiceParsingKeys.MovieKeys.TOTAL_PAGES);
            String total_result = jsonObject.getString(WebServiceParsingKeys.MovieKeys.TOTAL_RESULTS);

            JSONArray jsonArray = jsonObject.getJSONArray(WebServiceParsingKeys.MovieKeys.RESULTS);

            //set Time
            Calendar dayTime = new GregorianCalendar();
            Date date = new Date();
            dayTime.setTime(date);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject origArray = jsonArray.getJSONObject(i);

                String title = origArray.getString(WebServiceParsingKeys.MovieKeys.TITLE);
                String path = origArray.getString(WebServiceParsingKeys.MovieKeys.POSTER_PATH);
                String popularity = origArray.getString(WebServiceParsingKeys.MovieKeys.POPULARITY);
                String vote_avarage = origArray.getString(WebServiceParsingKeys.MovieKeys.VOTE_AVERAGE);

                MovieItem temp = new MovieItem(title, path, popularity, vote_avarage);
                temp.setTotal_pages(total_pages);
                temp.setTotal_results(total_result);

                temp.setOverview(origArray.getString(WebServiceParsingKeys.MovieKeys.OVERVIEW));
                temp.setRelease_date(origArray.getString(WebServiceParsingKeys.MovieKeys.RELEASE_DATE));
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
    private static String getReadableDateString(long time) {
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
