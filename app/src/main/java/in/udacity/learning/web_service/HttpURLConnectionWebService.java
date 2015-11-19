package in.udacity.learning.web_service;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import in.udacity.learning.constant.AppConstant;
import in.udacity.learning.keys.ApiKeys;
import in.udacity.learning.logger.L;
import in.udacity.learning.populermovie.app.BuildConfig;
import in.udacity.learning.populermovie.app.activities.MyApplication;

/**
 * Created by Lokesh on 06-09-2015.
 */
public class HttpURLConnectionWebService {

    private String TAG = HttpURLConnectionWebService.class.getName();
    private String sort_by = "popularity.desc"; //default Value
    private String primary_release_year = "2015";
    private String requestedPage = "1";

    public HttpURLConnectionWebService() {
    }

    public HttpURLConnectionWebService(String sort_by, String primary_release_year, String requestedPage) {
        this.sort_by = sort_by;
        this.primary_release_year = primary_release_year;
        this.requestedPage = requestedPage;
    }

    /* Tag is only for marking which class is calling this method*/
    public String getMovieJSON(String TAG) {
         /* Take an URL Object*/
        try {

            Uri builtUri = Uri.parse(WebServiceURL.baseURL).buildUpon()
                    .appendQueryParameter(WebServiceURL.API_KEY, BuildConfig.OPEN_MOVIE_API_KEY)
                    .appendQueryParameter(WebServiceURL.PRIMARY_RELEASE_YEAR, primary_release_year)
                    .appendQueryParameter(WebServiceURL.SORT_BY, sort_by)
                    .appendQueryParameter(WebServiceURL.PAGES, requestedPage).build();
            URL url = new URL(builtUri.toString());

            String json = getJSONString(url);
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Tag is only for marking which class is calling this method*/
    public String getTrailerJSON(String movieId) {
        try {

            Uri builtUri = Uri.parse(WebServiceURL.baseURLTrailer).buildUpon().appendPath(movieId).appendPath("videos")
                    .appendQueryParameter(WebServiceURL.API_KEY, BuildConfig.OPEN_MOVIE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            String json = getJSONString(url);
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Tag is only for marking which class is calling this method*/
    public String getReviewJSON(String movieId) {
        try {

            Uri builtUri = Uri.parse(WebServiceURL.baseURLTrailer).buildUpon().appendPath(movieId).appendPath("reviews")
                    .appendQueryParameter(WebServiceURL.API_KEY, BuildConfig.OPEN_MOVIE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            String json = getJSONString(url);
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJSONString(URL url) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
         /* Take an URL Object*/
        try {

                /* */
            if (AppConstant.DEVELOPER)
                Log.v(TAG, url.toString());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(4000);
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + " ");
            }

            if (stringBuffer.length() == 0) {
                return null;
            }
            /*Close Input Stream*/
            if (inputStream != null)
                inputStream.close();

            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            if (AppConstant.DEBUG)
                L.lToast(MyApplication.getInstance().getContext(), e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();

            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (final Exception e) {
                    L.lToast(MyApplication.getInstance().getContext(), e.toString());
                }
        }
        return null;
    }
}
