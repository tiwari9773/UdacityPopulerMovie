package in.udacity.learning.populermovie.app.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.udacity.learning.adapter.RecycleMarginDecoration;
import in.udacity.learning.adapter.TrailerViewAdapter;
import in.udacity.learning.constant.AppConstant;
import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.listener.OnTrailerClickListener;
import in.udacity.learning.model.ReviewItem;
import in.udacity.learning.model.ReviewResult;
import in.udacity.learning.model.TrailerItem;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.network.CheckNetworkConnection;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.populermovie.app.fragment.MainFragment;
import in.udacity.learning.web_service.HttpURLConnectionWebService;
import in.udacity.learning.web_service.JSONParser;
import in.udacity.learning.web_service.WebServiceURL;

/**
 * Created by Lokesh on 17-09-2015.
 */
public class MovieDetailActivityMobile extends AppCompatActivity implements OnTrailerClickListener {

    private final String TAG = getClass().getName();

    /* Toolbar*/
    private Toolbar mToolbar;

    /*Progress Dialog*/
    private ProgressDialog dialog;

    /*Selected Movie App*/
    private MovieItem item;

    /*Holds All Trailer view of Movie*/
    private String mTrailerId = "No Trailer";

    /*Boolean is present in database*/
    private boolean isAlreadyFavourated = false;

    /*For dynamic Color Change*/
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.tv_original_title)
    TextView tvTitle;
    @Bind(R.id.tv_overview)
    TextView tvOverview;
    @Bind(R.id.tv_language)
    TextView tvLanguage;
    @Bind(R.id.tv_release_date)
    TextView tvReleaseDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Problem: In this page UI gets little bit scrolled automatically and overview hide
        * How to find problem and solve*/
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initialise(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share:
                startActivity(createShareForecastIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");

        String trailerURL = "No Trailer";
        if (mTrailerId != null && !mTrailerId.equals("No Trailer"))
            trailerURL = WebServiceURL.youTubeBaseUrl + mTrailerId;

        shareIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle() + "\n" + trailerURL);
        return shareIntent;
    }

    private void initialise(Bundle savedInstanceState) {

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Initialise Collapsing Toolbar*/
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        /*Get The Item Form Intent*/
        item = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

        if (item != null) {
           /*Setup title of Image*/
            setTitle(item.getTitle());

            tvTitle.setText(item.getTitle());
            tvOverview.setText(item.getOverview());
            tvLanguage.setText(item.getOriginal_language());
            tvReleaseDate.setText(item.getRelease_date());

            final ImageView ivBanner = (ImageView) findViewById(R.id.iv_movie_poster);

        /*Set Rating bar out of Five*/
            RatingBar ratingBar = (RatingBar) findViewById(R.id.rb_ratingbar);
            float ratingval = Float.parseFloat(item.getVote_average()) / 2;
            ratingBar.setRating(ratingval);

            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            /*Is movie already Favourite*/
            Uri uri = MovieContract.FavouriteMovie.buildFavouriteUriWithServer(Integer.parseInt(item.getServerId()));
            Cursor c = getBaseContext().getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                isAlreadyFavourated = true;
               /*Change Icon also */
                fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_black_18dp));
            } else {
                isAlreadyFavourated = false;
               /*Change Icon also */
                fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_black_18dp));
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isAlreadyFavourated) {
                        isAlreadyFavourated = false;

                        /*Remove fraom database*/
                        Uri uri = MovieContract.FavouriteMovie.buildFavouriteUriWithServer(Integer.parseInt(item.getServerId()));
                        int i = getContentResolver().delete(uri, null, null);
                        if (i > 0) {
                            if (AppConstant.DEBUG)
                                Toast.makeText(MovieDetailActivityMobile.this, i + " Deleted", Toast.LENGTH_SHORT).show();
                        }

                            /*Change Icon also */
                        fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_black_18dp));

                    } else {
                        try {
                             /*Change Icon also */
                            fab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_black_18dp));
                            isAlreadyFavourated = true;

                            FileOutputStream fileOutputStream = openFileOutput(item.getServerId() + ".jpg", MODE_PRIVATE);

                            Bitmap bitmap = convertToBitMap(ivBanner.getDrawable(), ivBanner.getWidth(), ivBanner.getHeight());
                            //Bitmap bitmap = ((BitmapDrawable) ivBanner.getDrawable()).getBitmap();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
                            File file = getFileStreamPath(item.getServerId() + ".jpg");
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
                        cv.put(MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID, item.getServerId());
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

                        if (AppConstant.DEBUG)
                            Log.d(TAG, "onClick " + uri.toString() + " Values " + cv.toString());
                    }
                }

            });

          /* Check Trailer Values and link from server*/
            new CheckNetworkConnection(this, false, new CheckNetworkConnection.OnConnectionCallback() {

                @Override
                public void onConnectionSuccess() {
                    if (AppConstant.DEBUG)
                        Toast.makeText(MovieDetailActivityMobile.this, "onConnectionSuccess()", Toast.LENGTH_SHORT).show();

                    new FetchTrailerList().execute(new String[]{item.getServerId(), item.getPoster_path()});
                    new FetchReviewList().execute(new String[]{item.getServerId()});

                }

                @Override
                public void onConnectionFail(String msg) {
                    if (AppConstant.DEBUG)
                        Toast.makeText(MovieDetailActivityMobile.this, "onConnectionFail()", Toast.LENGTH_SHORT).show();
                }
            }).execute();

            //Update Image with Big Image
            /* Change Width of poster Imagecso that it should not look bad*/
            item.setPoster_path(item.getPoster_path().replace("w185", "w342"));
            Glide.with(this)
                    .load(item.getPoster_path()).asBitmap()
                    .listener(
                            new RequestListener<String, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    Palette.from(resource).generate(
                                            new Palette.PaletteAsyncListener() {
                                                @Override
                                                public void onGenerated(Palette palette) {
                                                    int primary = R.color.red;
                                                    int colorPrimaryDark = R.color.red;
                                                    int extractedColorPrimary = palette.getVibrantColor(primary);
                                                    int extractedDarkColor = palette.getDarkVibrantColor(colorPrimaryDark);

                                                    collapsingToolbarLayout.setContentScrimColor(extractedColorPrimary);
                                                    collapsingToolbarLayout.setStatusBarScrimColor(extractedDarkColor);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        //getWindow().setStatusBarColor(extractedDarkColor);
                                                        getWindow().setNavigationBarColor(extractedColorPrimary);
                                                    }
                                                }
                                            }

                                    );
                                    return false;
                                }
                            }
                    )
                    .centerCrop()
                    .placeholder(MainFragment.drawable)
                    .into(ivBanner);
        }


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
        protected List<TrailerItem> doInBackground(String... params) {
            String movieId = params[0];
            String path = params[1];
            String jsonString = new HttpURLConnectionWebService().getTrailerJSON(movieId);
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
        }
    }

    class FetchReviewList extends AsyncTask<String, String, List<ReviewItem>> {

        @Override
        protected List<ReviewItem> doInBackground(String... params) {
            String moviewId = params[0];
            List<ReviewItem> movieItems = new HttpURLConnectionWebService().getListReviewJSON(moviewId);
            return movieItems;
        }

        @Override
        protected void onPostExecute(List<ReviewItem> items) {
            super.onPostExecute(items);

            if (items != null && items.size() > 0) {
                setReview(items);
            }
        }
    }

    /*Set Trailer of movie*/
    private void setTrailerView(List<TrailerItem> item) {

        /*Initialise with first key of you tube video*/
        mTrailerId = item.get(0).getKey();

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
        List<ReviewResult> it = item.get(0).getResults();

        TextView author = (TextView) findViewById(R.id.tv_author);
        TextView content = (TextView) findViewById(R.id.tv_content);
        if (it.size() > 0) {
            author.setText(it.get(0).getAuthor());
            content.setText(it.get(0).getUri());
        }
    }
}