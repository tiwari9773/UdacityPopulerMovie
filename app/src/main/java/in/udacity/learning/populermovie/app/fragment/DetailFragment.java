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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
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
import java.util.ArrayList;
import java.util.List;

import in.udacity.learning.adapter.RecycleMarginDecoration;
import in.udacity.learning.adapter.TrailerViewAdapter;
import in.udacity.learning.constant.AppConstant;
import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.listener.OnTrailerClickListener;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.model.ReviewItem;
import in.udacity.learning.model.TrailerItem;
import in.udacity.learning.network.NetWorkInfoUtility;
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

    private ShareActionProvider mShareActionProvider;
    private MyBroadcastReciver receiver;

    /*Boolean is present in database*/
    private boolean isAlreadyFavourited = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiver = new MyBroadcastReciver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(AppConstant.FILTER_OBJECT));
    }

    public class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.DEBUG)
                Toast.makeText(getContext(), "completed Load of List", Toast.LENGTH_SHORT).show();
            item = intent.getParcelableExtra(AppConstant.OBJECT);
            initialise(view);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (receiver != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Bundle b = getArguments();
        if (b != null) {
            item = b.getParcelable(AppConstant.OBJECT);
            if (item != null)
                initialise(view);
        }
        return view;
    }

    private void initialise(View view) {

        final ImageView ivBanner = (ImageView) view.findViewById(R.id.iv_movie_poster);
        if (item != null) {
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_original_title);
            tvTitle.setText(item.getTitle());

            TextView tvOverview = (TextView) view.findViewById(R.id.tv_overview);
            tvOverview.setText(item.getOverview());

            TextView tvLanguage = (TextView) view.findViewById(R.id.tv_language);
            tvLanguage.setText(item.getOriginal_language());

            TextView tvReleaseDate = (TextView) view.findViewById(R.id.tv_release_date);
            tvReleaseDate.setText(item.getRelease_date());

            /*Set Rating bar out of Five*/
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_ratingbar);
            float ratingval = Float.parseFloat(item.getVote_average()) / 2;
            ratingBar.setRating(ratingval);

            final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

            /*Is movie already Favourite*/
            Uri uri = MovieContract.FavouriteMovie.buildFavouriteUriWithServer(Integer.parseInt(item.getServerId()));
            Cursor c = getActivity().getBaseContext().getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                isAlreadyFavourited = true;
               /*Change Icon also */
                fab.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_favorite_black_18dp));
            } else {
                isAlreadyFavourited = false;
               /*Change Icon also */
                fab.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_favorite_border_black_18dp));
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isAlreadyFavourited) {
                        isAlreadyFavourited = false;

                        /*Remove fraom database*/
                        Uri uri = MovieContract.FavouriteMovie.buildFavouriteUriWithServer(Integer.parseInt(item.getServerId()));
                        int i = getActivity().getContentResolver().delete(uri, null, null);
                        if (i > 0) {
                            if (AppConstant.DEBUG)
                                Toast.makeText(getActivity(), i + " Deleted", Toast.LENGTH_SHORT).show();
                        }

                            /*Change Icon also */
                        fab.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_favorite_border_black_18dp));

                    } else {
                        try {

                           /*Change Icon also */
                            fab.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_favorite_black_18dp));
                            isAlreadyFavourited = true;

                            FileOutputStream fileOutputStream = getActivity().openFileOutput(item.getServerId() + ".jpg", getActivity().MODE_PRIVATE);
                            Bitmap bitmap = convertToBitMap(ivBanner.getDrawable(), ivBanner.getWidth(), ivBanner.getHeight());
                            //Bitmap bitmap = ((BitmapDrawable) ivBanner.getDrawable()).getBitmap();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
                            File file = getActivity().getFileStreamPath(item.getServerId() + ".jpg");
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

                        Uri uri = getActivity().getContentResolver().insert(MovieContract.FavouriteMovie.CONTENT_URI, cv);
                        if (AppConstant.DEBUG) {
                            Log.d(TAG, "onClick " + uri.toString() + " Values " + cv.toString());
                            Toast.makeText(getActivity(), uri + " Inserted", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });

         /* Check Trailer Values and link from server*/
            if (new NetWorkInfoUtility().isNetWorkAvailableNow(getActivity())) {
                new FetchTrailerList().execute(new String[]{item.getServerId(), item.getPoster_path()});
                new FetchReviewList().execute(new String[]{item.getServerId()});
            }

            //Update Image with Big Image
        /* Change Width of poster so that it should not look bad*/
            item.setPoster_path(item.getPoster_path().replace("w185", "w342"));
            Glide.with(this)
                    .load(item.getPoster_path())
                    .centerCrop()
                    .placeholder(MainFragment.drawable)
                    .fitCenter()
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
        }
    }

    /*Set Trailer of movie*/
    private void setTrailerView(List<TrailerItem> item) {
        /*Trailer Setup*/
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_trailer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        RecycleMarginDecoration recycleMarginDecoration = new RecycleMarginDecoration(getContext());
        recyclerView.addItemDecoration(recycleMarginDecoration);
        recyclerView.setHasFixedSize(true);

        TrailerViewAdapter adapter = new TrailerViewAdapter(item, this);
        recyclerView.setAdapter(adapter);
    }

    /* Review of Author*/
    private void setReview(List<ReviewItem> item) {
        TextView author = (TextView) view.findViewById(R.id.tv_author);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        author.setText(item.get(0).getAuthor());
        content.setText(item.get(0).getUri());
    }

    // Dialog Progress
    private void progressLoading() {
        dialog = new ProgressDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareForecastIntent());
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        if (item != null)
            shareIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle() + "\n" + item.getOverview());
        return shareIntent;
    }

}
