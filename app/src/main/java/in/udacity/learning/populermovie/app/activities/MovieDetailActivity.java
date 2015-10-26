package in.udacity.learning.populermovie.app.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.dbhelper.MovieProvider;
import in.udacity.learning.populermovie.app.fragment.FragmentMain;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.web_service.WebServiceURL;

/**
 * Created by Lokesh on 17-09-2015.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();

    /* Test*/
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initialise(savedInstanceState);
    }

    private void initialise(Bundle savedInstanceState) {

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MovieItem item = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

        /*Setup title of Image*/
        setTitle(item.getTitle());

        ImageView ivBanner = (ImageView) findViewById(R.id.iv_movie_poster);
        ivBanner.setImageDrawable(FragmentMain.drawable);

        TextView tvTitle = (TextView)findViewById(R.id.tv_original_title);
        tvTitle.setText(item.getTitle());

        TextView tvOverview = (TextView)findViewById(R.id.tv_overview);
        tvOverview.setText(item.getOverview());

        TextView tvUserRating = (TextView)findViewById(R.id.tv_rating);
        tvUserRating.setText(item.getVote_average());

        TextView tvReleaseDate = (TextView)findViewById(R.id.tv_release_date);
        tvReleaseDate.setText(item.getRelease_date());

        //Update Image with Big Image
        Glide.with(this)
                .load(WebServiceURL.baseURLPoster + "/" + item.getPoster_path())
                .centerCrop()
                .placeholder(FragmentMain.drawable)
                .crossFade()
                .into(ivBanner);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID, item.getId());
                cv.put(MovieContract.FavouriteMovie.COL_RELEASE_DATE, 1234);
                cv.put(MovieContract.FavouriteMovie.COL_ORIGINAL_LANGUAGE, item.getOriginal_language());
                cv.put(MovieContract.FavouriteMovie.COL_ORIGINAL_TITLE, item.getOriginal_title());
                cv.put(MovieContract.FavouriteMovie.COL_OVERVIEW, item.getOverview());
                cv.put(MovieContract.FavouriteMovie.COL_POSTER_PATH, item.getPoster_path());
                cv.put(MovieContract.FavouriteMovie.COL_TITLE, item.getTitle());
                cv.put(MovieContract.FavouriteMovie.COL_POPULARITY, item.getPopularity());
                cv.put(MovieContract.FavouriteMovie.COL_VOTE_AVERAGE, item.getVote_average());
                cv.put(MovieContract.FavouriteMovie.COL_VOTE_COUNT, item.getVote_count());

                Uri uri = getContentResolver().insert(MovieContract.FavouriteMovie.CONTENT_URI,cv);
                Log.d(TAG, "onClick " + uri.toString()+ " Values "+cv.toString());
            }
        });

    }
}
