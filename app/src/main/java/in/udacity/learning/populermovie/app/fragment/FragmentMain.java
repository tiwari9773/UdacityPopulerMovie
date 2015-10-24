package in.udacity.learning.populermovie.app.fragment;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.udacity.learning.adapter.MovieViewAdapter;
import in.udacity.learning.framework.OnMovieItemClickListener;
import in.udacity.learning.listener.RecycleEndlessScrollListener;
import in.udacity.learning.logger.L;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.network.NetWorkInfoUtility;
import in.udacity.learning.populermovie.app.activities.MovieDetailActivity;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.web_service.HttpURLConnectionWebService;
import in.udacity.learning.web_service.JSONParser;

/**
 * Created by Lokesh on 13-09-2015.
 */
public class FragmentMain extends Fragment implements OnMovieItemClickListener {
    private String TAG = getClass().getName();

    private MovieViewAdapter movieViewAdapter;
    private List<MovieItem> mItem = new ArrayList<>();

    /* Option available for sorting */
    private String sort_populer = "popularity.desc";
    private String sort_rating = "vote_average.desc";

    /* Current selected sorting , by default consider populer as sort */
    private String sort_order = sort_populer;

    /*Identify same is getting selecting or not*/
    private boolean isSortingOrderChange = false;

    private ProgressDialog dialog;
    public static Drawable drawable;

    /*Endless scroll listener*/
    RecycleEndlessScrollListener recycleEndlessScrollListener;

    // To reduce Overhead of continues toast to given delay by Timer
    private boolean isShowToast = true;

    // Timer which control toast display
    private Timer mTimer;

    public FragmentMain() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*By default considering user changed sorting order*/
        isSortingOrderChange = true;

        switch (item.getItemId()) {
            case R.id.action_sort_by_popularity:
                if (sort_order == sort_populer) {
                    isSortingOrderChange = false;
                    break;
                }
                sort_order = sort_populer;
                break;

            case R.id.action_sort_by_rating:
                if (sort_order == sort_rating) {
                    isSortingOrderChange = false;
                    break;
                }
                sort_order = sort_rating;
                break;
        }

        /* Refresh list with sorting, Only if sorting order is changed by user*/
        if (isSortingOrderChange) {
            updateMovieList(sort_order);
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initialise(view);

        // Timer which control over flooded toast message
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                isShowToast = true;
            }
        }, 1000, 500);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //for second time when user press back button we should no load again
        if (mItem == null || mItem.size() == 0)
            updateMovieList(sort_order);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTimer != null)
            mTimer.cancel();
    }

    private void initialise(View view) {

        recycleEndlessScrollListener = new RecycleEndlessScrollListener() {
            @Override
            public void onLoadMore(int current_page) {
                updateMovieList(sort_order, current_page);
            }
        };


        RecyclerView recycleView = (RecyclerView) view.findViewById(R.id.rv_movie_list);
        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recycleView.setLayoutManager(layoutManager);

        movieViewAdapter = new MovieViewAdapter(mItem, this);
        recycleView.setAdapter(movieViewAdapter);

        /*On Scroll Listener*/
        recycleView.setOnScrollListener(recycleEndlessScrollListener);
    }

    private void updateMovieList(String sort_order) {
        /* By default considering first page*/
        int current_page = 1;
        updateMovieList(sort_order, current_page);
    }

    private void updateMovieList(String sortOrder, int current_page) {
        //
        String param[] = new String[]{sortOrder, "2015", String.valueOf(current_page)};

        if (new NetWorkInfoUtility().isNetWorkAvailableNow(getContext())) {
            new FetchMovieList().execute(param);
        } else {
            L.lToast(getContext(), getString(R.string.msg_internet_status));
        }
    }


    @Override
    public void onClickMovieThumbnail(View view, int position) {

        ImageView img = (ImageView) view;
        drawable = img.getDrawable();
        MovieItem temp = mItem.get(position);

        Intent in = new Intent(getActivity(), MovieDetailActivity.class);
        in.putExtra(Intent.EXTRA_TEXT, temp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String trasnition = getContext().getString(R.string.transition_thumbnail_to_poster);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(), view, trasnition);

            getActivity().startActivity(in, options.toBundle());
        } else {
            startActivity(in);
        }
    }

    // Dialog Progress
    private void progressLoading() {
        dialog = new ProgressDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    class FetchMovieList extends AsyncTask<String, String, List<MovieItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressLoading();
        }

        @Override
        protected List<MovieItem> doInBackground(String... params) {
            String sort_by = params[0];
            String realese_year = params[1];
            String pages = params[2];

            String jsonString = new HttpURLConnectionWebService(sort_by, realese_year, pages).getMovieJSON(TAG);
            if (jsonString != null) {
                List<MovieItem> movieItems = JSONParser.parseMovieList(jsonString);
                return movieItems;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(List<MovieItem> movieItems) {
            super.onPostExecute(movieItems);

            int position_start = 0;
            int item_count = 0;

            /*If sorting order chagne everything will be reset as per new sorting list*/
            if (isSortingOrderChange) {
                    /* Reset Sorting Order*/
                isSortingOrderChange = false;

                    /* Clear list new list will be added*/
                movieViewAdapter.resetList();

                /* Clear List which is being displayed*/
                populateList(new ArrayList<MovieItem>());

                /*Clear Local holding of List*/
                mItem.clear();
            }

            if (movieItems != null) {

                /*If size is greater than zero then only we can get position zero*/
                if (movieItems.size() > 0)
                    recycleEndlessScrollListener.setMaxPages(Integer.parseInt(movieItems.get(0).getTotal_pages()));

                if (mItem != null) {
                    position_start = mItem.size();
                    item_count = movieItems.size();
                    mItem.addAll(movieItems);
                }
            }

            if (mItem != null && movieItems != null) {
                movieViewAdapter.setLsItem(movieItems);
                movieViewAdapter.notifyItemRangeInserted(position_start, item_count);
            }

            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }
    }

    /**/
    private void populateList(List<MovieItem> movieItems) {
        movieViewAdapter.setLsItem(movieItems);
        movieViewAdapter.notifyDataSetChanged();
    }

}
