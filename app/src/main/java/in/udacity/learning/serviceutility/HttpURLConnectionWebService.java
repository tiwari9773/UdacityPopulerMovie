package in.udacity.learning.serviceutility;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import in.udacity.learning.constants.AppConstant;
import in.udacity.learning.keys.ApiKeys;
import in.udacity.learning.logger.L;
import in.udacity.learning.populermovie.app.MyApplication;

/**
 * Created by Lokesh on 06-09-2015.
 */
public class HttpURLConnectionWebService {

    String mode = "discover";
    String sort_by = "popularity.desc";
    int year = 2015;

    public HttpURLConnectionWebService(String mode, String sort_by, int year) {
        this.mode = mode;
        this.sort_by = sort_by;
        this.year = year;
    }

    public String getJSON(String TAG) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
         /* Take an URL Object*/
        try {

            Uri builtUri = Uri.parse(WebServiceURL.baseURL).buildUpon()
                    .appendQueryParameter(WebServiceURL.API_KEY, ApiKeys.movie_api_keys)
                    .appendQueryParameter(WebServiceURL.YEAR, mode)
                    .appendQueryParameter(WebServiceURL.SORT_BY, sort_by).build();
            URL url = new URL(builtUri.toString());
//https://api.themoviedb.org/3/discover/movie?api_key=0fa829fc888260d7316187ab3e9dc115&year=discover&sort_by=popularity.desc
                /* */
            if (AppConstant.DEVELOPER)
                Log.v(TAG, builtUri.toString());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + " " + "\\n");
            }

            if (stringBuffer.length() == 0) {
                return null;
            }

            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            L.lToast(MyApplication.getContext(), e.toString());
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();

            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (final Exception e) {
                    L.lToast(MyApplication.getContext(), e.toString());
                }
        }
        return null;
    }
}
