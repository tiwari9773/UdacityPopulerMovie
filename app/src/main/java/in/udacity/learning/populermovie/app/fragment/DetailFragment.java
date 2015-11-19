package in.udacity.learning.populermovie.app.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.udacity.learning.adapter.RecycleMarginDecoration;
import in.udacity.learning.adapter.TrailerViewAdapter;
import in.udacity.learning.model.ReviewResult;
import in.udacity.learning.network.CheckNetworkConnection;
import in.udacity.learning.constant.AppConstant;
import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.listener.OnTrailerClickListener;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.model.ReviewItem;
import in.udacity.learning.model.TrailerItem;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.web_service.HttpURLConnectionWebService;
import in.udacity.learning.web_service.JSONParser;
import in.udacity.learning.web_service.WebServiceURL;

/**
 * Created by Lokesh on 01-11-2015.
 */
public class DetailFragment extends Fragment implements OnTrailerClickListener {
    /*TAG name*/
    public static final String TAG = DetailFragment.class.getName();

    /*Progress Dialog*/
    private ProgressDialog dialog;

    /*Selected Movie App*/
    private MovieItem item;

    /*View for display*/
    private View view;

    /*It receive info as panel list is loaded completely*/
    private MyBroadcastReciver receiver;

    /*Boolean is present in database*/
    private boolean isAlreadyFavourited = false;

    /*Holds All Trailer view of Movie*/
    private String mTrailerId = "No Trailer";

    @Bind(R.id.tv_original_title)
    TextView tvTitle;
    @Bind(R.id.tv_overview)
    TextView tvOverview;
    @Bind(R.id.tv_language)
    TextView tvLanguage;
    @Bind(R.id.tv_release_date)
    TextView tvReleaseDate;
    @Bind(R.id.iv_movie_poster)
    ImageView ivBanner;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiver = new MyBroadcastReciver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(AppConstant.FILTER_OBJECT));
    }

    public class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            item = intent.getParcelableExtra(AppConstant.OBJECT);
            initialise(view);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (receiver != null)
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);

        Bundle b = getArguments();
        if (b != null) {
            item = b.getParcelable(AppConstant.OBJECT);
            if (item != null)
                initialise(view);
        }
        return view;
    }

    private void initialise(View view) {


        if (item != null) {
            TextView tv = (TextView) view.findViewById(R.id.tv_no_movie_selected);
            tv.setVisibility(View.GONE);

            tvTitle.setText(item.getTitle());
            tvOverview.setText(item.getOverview());
            tvLanguage.setText(item.getOriginal_language());
            tvReleaseDate.setText(item.getRelease_date());

            /*Set Rating bar out of Five*/
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_ratingbar);
            float ratingval = Float.parseFloat(item.getVote_average()) / 2;
            ratingBar.setRating(ratingval);

            final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

            /*Is movie already Favourite*/
            Uri uri = MovieContract.FavouriteMovie.buildFavouriteUriWithServer(Integer.parseInt(item.getServerId()));
            Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                isAlreadyFavourited = true;
               /*Change Icon also */
                fab.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_favorite_black_18dp));
            } else {
                isAlreadyFavourited = false;
               /*Change Icon also */
                fab.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_favorite_border_black_18dp));
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isAlreadyFavourited) {
                        isAlreadyFavourited = false;

                        /*Remove from database*/
                        Uri uri = MovieContract.FavouriteMovie.buildFavouriteUriWithServer(Integer.parseInt(item.getServerId()));
                        int i = getContext().getContentResolver().delete(uri, null, null);
                        if (i > 0) {
                            if (AppConstant.DEBUG)
                                Toast.makeText(getContext(), i + " Deleted", Toast.LENGTH_SHORT).show();
                        }

                            /*Change Icon also */
                        fab.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_favorite_border_black_18dp));

                    } else {
                        try {

                           /*Change Icon also */
                            fab.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_favorite_black_18dp));
                            isAlreadyFavourited = true;

                            FileOutputStream fileOutputStream = getContext().openFileOutput(item.getServerId() + ".jpg", getContext().MODE_PRIVATE);
                            Bitmap bitmap = convertToBitMap(ivBanner.getDrawable(), ivBanner.getDrawable().getIntrinsicWidth(), ivBanner.getDrawable().getIntrinsicHeight());
                            //Bitmap bitmap = ((BitmapDrawable) ivBanner.getDrawable()).getBitmap();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
                            File file = getContext().getFileStreamPath(item.getServerId() + ".jpg");
                            String localPath = file.getAbsolutePath();

                            item.setPoster_path(localPath);

                            /*House Keeping Operation*/
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            isAlreadyFavourited = true;

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

                        Uri uri = getContext().getContentResolver().insert(MovieContract.FavouriteMovie.CONTENT_URI, cv);
                        if (AppConstant.DEBUG) {
                            Log.d(TAG, "onClick " + uri.toString() + " Values " + cv.toString());
                            Toast.makeText(getContext(), uri + " Inserted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

          /* Check Trailer Values and link from server*/
            new CheckNetworkConnection(getContext(), false, new CheckNetworkConnection.OnConnectionCallback() {

                @Override
                public void onConnectionSuccess() {
                    new FetchTrailerList().execute(new String[]{item.getServerId(), item.getPoster_path()});
                    new FetchReviewList().execute(new String[]{item.getServerId()});
                }

                @Override
                public void onConnectionFail(String msg) {
                    if (AppConstant.DEBUG && isAdded())
                        Toast.makeText(getContext(), "onConnectionFail()", Toast.LENGTH_SHORT).show();
                }
            }).execute();

            //Update Image with Big Image
        /* Change Width of poster so that it should not look bad*/
            item.setPoster_path(item.getPoster_path().replace("w185", "w342"));
            Glide.with(this)
                    .load(item.getPoster_path())
                    .centerCrop()
                    .placeholder(MainFragment.drawable)
                    .fitCenter()
                    .into(ivBanner);
        } else {

            /* It will refresh the view from previous selection*/
            ivBanner.setImageResource(R.mipmap.ic_launcher);
            TextView tv = (TextView) view.findViewById(R.id.tv_no_movie_selected);
            tv.setVisibility(View.VISIBLE);
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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_trailer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        RecycleMarginDecoration recycleMarginDecoration = new RecycleMarginDecoration(view.getContext());
        if (recycleMarginDecoration != null) {
            recyclerView.addItemDecoration(recycleMarginDecoration);
        }
        recyclerView.setHasFixedSize(true);

        TrailerViewAdapter adapter = new TrailerViewAdapter(item, this);
        recyclerView.setAdapter(adapter);
    }

    /* Review of Author*/
    private void setReview(List<ReviewItem> item) {
        List<ReviewResult> it = item.get(0).getResults();

        TextView author = (TextView) view.findViewById(R.id.tv_author);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        if (it.size() > 0) {
            author.setText(it.get(0).getAuthor());
            content.setText(it.get(0).getUri());
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share:
                startActivity(createShareForecastIntent());
        }

        if (AppConstant.DEBUG_DONE)
            Toast.makeText(getContext(), "Inside Detail Fragment", Toast.LENGTH_SHORT).show();

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

}
