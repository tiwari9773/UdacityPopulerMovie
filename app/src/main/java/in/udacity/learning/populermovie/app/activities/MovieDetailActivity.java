package in.udacity.learning.populermovie.app.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.udacity.learning.adapter.RecycleMarginDecoration;
import in.udacity.learning.adapter.TrailerViewAdapter;
import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.dbhelper.MovieProvider;
import in.udacity.learning.listener.OnTrailerClickListener;
import in.udacity.learning.model.ReviewItem;
import in.udacity.learning.model.TrailerItem;
import in.udacity.learning.network.NetWorkInfoUtility;
import in.udacity.learning.populermovie.app.fragment.FragmentMain;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.web_service.HttpURLConnectionWebService;
import in.udacity.learning.web_service.JSONParser;
import in.udacity.learning.web_service.WebServiceURL;

/**
 * Created by Lokesh on 17-09-2015.
 */
public class MovieDetailActivity extends AppCompatActivity implements OnTrailerClickListener {

    private final String TAG = getClass().getName();

    /* Toolbar*/
    private Toolbar mToolbar;

    /*Progress Dialog*/
    private ProgressDialog dialog;

    /*Selected Movie App*/
    private MovieItem item;

    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initialise(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareForecastIntent());
        return super.onCreateOptionsMenu(menu);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle());
        return shareIntent;
    }

    private void initialise(Bundle savedInstanceState) {

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        item = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

        /*Setup title of Image*/
        setTitle(item.getTitle());

        final ImageView ivBanner = (ImageView) findViewById(R.id.iv_movie_poster);

        TextView tvTitle = (TextView) findViewById(R.id.tv_original_title);
        tvTitle.setText(item.getTitle());

        TextView tvOverview = (TextView) findViewById(R.id.tv_overview);
        tvOverview.setText(item.getOverview());

        TextView tvLanguage = (TextView) findViewById(R.id.tv_language);
        tvLanguage.setText(item.getOriginal_language());

        TextView tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvReleaseDate.setText(item.getRelease_date());

        /*Set Rating bar out of Five*/
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rb_ratingbar);
        float ratingval = Float.parseFloat(item.getVote_average()) / 2;
        ratingBar.setRating(ratingval);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    FileOutputStream fileOutputStream = openFileOutput(item.getId() + ".jpg", MODE_PRIVATE);

                    Bitmap bitmap = convertToBitMap(ivBanner.getDrawable(), ivBanner.getWidth(), ivBanner.getHeight());
                    //Bitmap bitmap = ((BitmapDrawable) ivBanner.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
                    File file = getFileStreamPath(item.getId() + ".jpg");
                    String localPath = file.getAbsolutePath();

                    item.setPoster_path(localPath);

                    /*House Keeping Operation*/
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID, item.getId());
                cv.put(MovieContract.FavouriteMovie.COL_RELEASE_DATE, item.getRelease_date());
                cv.put(MovieContract.FavouriteMovie.COL_ORIGINAL_LANGUAGE, item.getOriginal_language());
                cv.put(MovieContract.FavouriteMovie.COL_ORIGINAL_TITLE, item.getOriginal_title());
                cv.put(MovieContract.FavouriteMovie.COL_OVERVIEW, item.getOverview());
                cv.put(MovieContract.FavouriteMovie.COL_POSTER_PATH, item.getPoster_path());
                cv.put(MovieContract.FavouriteMovie.COL_THUMBNAIL_PATH, item.getPoster_path());
                cv.put(MovieContract.FavouriteMovie.COL_TITLE, item.getTitle());
                cv.put(MovieContract.FavouriteMovie.COL_POPULARITY, item.getPopularity());
                cv.put(MovieContract.FavouriteMovie.COL_VOTE_AVERAGE, item.getVote_average());
                cv.put(MovieContract.FavouriteMovie.COL_VOTE_COUNT, item.getVote_count());

                Uri uri = getContentResolver().insert(MovieContract.FavouriteMovie.CONTENT_URI, cv);
                Log.d(TAG, "onClick " + uri.toString() + " Values " + cv.toString());
            }
        });


         /* Check Trailer Values and link from server*/
        if (new NetWorkInfoUtility().isNetWorkAvailableNow(this)) {
            new FetchTrailerList().execute(new String[]{item.getId(), item.getPoster_path()});
            new FetchReviewList().execute(new String[]{item.getId()});
        }

        //Update Image with Big Image
        /* Change Width of poster so that it should not look bad*/

        item.setPoster_path(item.getPoster_path().replace("w185", "w342"));
        Glide.with(this)
                .load(item.getPoster_path())
                .centerCrop()
                .placeholder(FragmentMain.drawable)
                .crossFade()
                .into(ivBanner);

    }

    /*Convert ImageView to Bitmap*/
    private Bitmap convertToBitMap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onMovieTrailer(String youTubeKey) {
        Toast.makeText(MovieDetailActivity.this, youTubeKey + "", Toast.LENGTH_SHORT).show();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youTubeKey));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(WebServiceURL.youTubeBaseUrl + youTubeKey));
            startActivity(intent);
        }
    }

    class FetchTrailerList extends AsyncTask<String, String, List<TrailerItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressLoading();
        }

        @Override
        protected List<TrailerItem> doInBackground(String... params) {
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
        protected void onPostExecute(List<TrailerItem> items) {
            super.onPostExecute(items);

            if (items != null && items.size() > 0) {
                setTrailerView(items);
            }
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }
    }

    class FetchReviewList extends AsyncTask<String, String, List<ReviewItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                progressLoading();
        }

        @Override
        protected List<ReviewItem> doInBackground(String... params) {
            String moviewId = params[0];
            String jsonString = new HttpURLConnectionWebService().getReviewJSON(moviewId);
            if (jsonString != null) {
                List<ReviewItem> movieItems = JSONParser.parseReviewList(jsonString);
                return movieItems;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(List<ReviewItem> items) {
            super.onPostExecute(items);

            if (items != null && items.size() > 0) {
                setReview(items);
            }
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }
    }

    /*Set Trailer of movie*/
    private void setTrailerView(List<TrailerItem> item) {
        /*Trailer Setup*/
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_trailer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        RecycleMarginDecoration recycleMarginDecoration = new RecycleMarginDecoration(this);
        recyclerView.addItemDecoration(recycleMarginDecoration);
        TrailerViewAdapter adapter = new TrailerViewAdapter(item, this);
        recyclerView.setAdapter(adapter);
    }

    /* Review of Author*/
    private void setReview(List<ReviewItem> item) {
        TextView author = (TextView) findViewById(R.id.tv_author);
        TextView content = (TextView) findViewById(R.id.tv_content);
        author.setText(item.get(0).getAuthor());
        content.setText(item.get(0).getUri());
    }

    // Dialog Progress
    private void progressLoading() {
        dialog = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

}
