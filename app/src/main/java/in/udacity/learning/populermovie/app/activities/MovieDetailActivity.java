package in.udacity.learning.populermovie.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.udacity.learning.populermovie.app.fragment.FragmentMain;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.web_service.WebServiceURL;

/**
 * Created by Lokesh on 17-09-2015.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
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

        MovieItem item = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

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

    }
}
