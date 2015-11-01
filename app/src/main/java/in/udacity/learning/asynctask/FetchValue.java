package in.udacity.learning.asynctask;

import android.os.AsyncTask;

import java.util.List;

import in.udacity.learning.model.MarkerItem;
import in.udacity.learning.model.TrailerItem;
import in.udacity.learning.web_service.HttpURLConnectionWebService;
import in.udacity.learning.web_service.JSONParser;

/**
 * Created by Lokesh on 01-11-2015.
 */
public class FetchValue extends AsyncTask<String, String, List<? extends MarkerItem>> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<? extends MarkerItem> doInBackground(String... params) {
        String moviewId = params[0];
        String path = params[1];
        String jsonString = new HttpURLConnectionWebService().getTrailerJSON(moviewId);
        if (jsonString != null) {
            List<TrailerItem> movieItems = JSONParser.parseTrailerList(jsonString, path);
            return movieItems;
        } else
            return null;

    }

    @Override
    protected void onPostExecute(List<? extends MarkerItem> markerItems) {
        super.onPostExecute(markerItems);
    }
}
