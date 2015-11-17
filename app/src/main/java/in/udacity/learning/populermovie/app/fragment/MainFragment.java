package in.udacity.learning.populermovie.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.udacity.learning.adapter.MovieViewAdapter;
import in.udacity.learning.adapter.RecycleMarginDecoration;
import in.udacity.learning.network.CheckNetworkConnection;
import in.udacity.learning.constant.AppConstant;
import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.listener.OnMovieItemClickListener;
import in.udacity.learning.listener.RecycleEndlessScrollListener;
import in.udacity.learning.logger.L;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.populermovie.app.activities.MainActivity;
import in.udacity.learning.web_service.HttpURLConnectionWebService;
import in.udacity.learning.web_service.JSONParser;

/**
 * Created by Lokesh on 13-09-2015.
 */
public class MainFragment extends Fragment implements OnMovieItemClickListener {
    private String TAG = getClass().getName();

    private MovieViewAdapter movieViewAdapter;
    private List<MovieItem> mItems = new ArrayList<>();

    /* Option available for sorting */
    private String sort_populer = "popularity.desc";
    private String sort_rating = "vote_average.desc";
    private String sort_favourite = "favourite";

    /* Current selected sorting , by default consider populer as sort */
    private String sort_order = sort_populer;

    /*Identify same is getting selecting or not*/
    private boolean isSortingOrderChange = false;

    /*To transfer Click Image to next page
    * Purposely didn't used approach to save on directory and send path, because it will delay the process
    * and create lag in animation of transition*/
    public static Drawable drawable;

    /*Empty View in RecyclerView*/
    private TextView tvEmptyTextView;

    /*Endless scroll listener*/
    private RecycleEndlessScrollListener recycleEndlessScrollListener;

    /*Network Change listener*/
    private NetWorkChangeListener netWorkChangeListener;

    /*Make first item selected first time*/
    private boolean isFirstSelected = false;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(MovieItem item, View clickedView);
    }

    /*Call back Reference for parent Activity*/
    private Callback referenceForCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        referenceForCallback = (Callback) activity;
    }

    public MainFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initialise(view);

        return view;
    }

    private void initialise(View view) {

        /* Empty View When no internet is present*/
        tvEmptyTextView = (TextView) view.findViewById(R.id.tv_empty_view);

         /*
        *Add Broadcast listener at last , If we register earlier then It might listen even before
        * we initialise our views
        */
        netWorkChangeListener = new NetWorkChangeListener();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        /*Local Broadcast manager For network notification*/
        getActivity().registerReceiver(netWorkChangeListener, intentfilter);

        recycleEndlessScrollListener = new RecycleEndlessScrollListener() {
            @Override
            public void onLoadMore(int current_page) {
                /*If Items are not sorted by favourite then only call server side else no need to try
                * because value will come from saved location*/
                if (!sort_order.equals(sort_favourite)) {
                    //add progress item and If list already contains one null that means Loading progress bar
                    // is already added so no need to add again if we remove !mItem.contains(null) it
                    // add progress bar multiple times.
                    if (mItems != null && !mItems.contains(null)) {
                        mItems.add(null);

                        if (AppConstant.DEBUG_DONE)
                            Toast.makeText(getActivity(), mItems.size() + "Loaded", Toast.LENGTH_SHORT).show();
                    }

                    movieViewAdapter.notifyItemInserted(mItems.size());
                    updateMovieList(sort_order, current_page);
                }
            }
        };

        RecyclerView recycleView = (RecyclerView) view.findViewById(R.id.rv_movie_list);
        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        int span = 2;
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), span);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setHasFixedSize(true);

        RecycleMarginDecoration marginDecoration = new RecycleMarginDecoration(getContext());
        recycleView.addItemDecoration(marginDecoration);
        movieViewAdapter = new MovieViewAdapter(mItems, this);
        recycleView.setAdapter(movieViewAdapter);

        /*On Scroll Listener*/
        recycleView.addOnScrollListener(recycleEndlessScrollListener);
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

                /*If There is no change as per record no need to initiate change*/
                if (sort_order.equals(sort_populer)) {
                    isSortingOrderChange = false;
                    return true;
                }

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Popular Movie");
                /*If new Order to sort then it is acceptable to call webservice*/
                sort_order = sort_populer;

               /*Clear OtherPanel if it is in tablet mode*/
                if (((MainActivity) getActivity()).ismTwoPane())
                    referenceForCallback.onItemSelected(null, null);

                /*Now list is new and completely refresh, so application make first one as Selected*/
                isFirstSelected = false;
                refreshNewMovieList(sort_order);
                return true;

            case R.id.action_sort_by_rating:
                if (sort_order.equals(sort_rating)) {
                    isSortingOrderChange = false;
                    return true;
                }

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Rated Movie");
                sort_order = sort_rating;

                 /*Clear OtherPanel if it is in tablet mode*/
                if (((MainActivity) getActivity()).ismTwoPane())
                    referenceForCallback.onItemSelected(null, null);

                /*Now list is new and completely refresh, so application make firstone as Selected*/
                isFirstSelected = false;
                refreshNewMovieList(sort_order);
                return true;
            case R.id.action_favourite:
                if (sort_order.equals(sort_favourite)) {
                    isSortingOrderChange = false;
                    return true;
                }
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Favourite");

                sort_order = sort_favourite;

                /*Clear OtherPanel if it is in tablet mode*/
                if (((MainActivity) getActivity()).ismTwoPane())
                    referenceForCallback.onItemSelected(null, null);

                /*Now list is new and completely refresh, so application make firstone as Selected*/
                isFirstSelected = false;
                refreshNewMovieList(sort_order);
                return true;
        }

        if (AppConstant.DEBUG_DONE)
            Toast.makeText(getActivity(), "Inside Main Fragment", Toast.LENGTH_SHORT).show();
        //Strictly Do not write here every time menu getting clicked even in second panel in tablet layout
        // it will get called execute so write it in every block
        //refreshNewMovieList(sort_order);
        return super.onOptionsItemSelected(item);
    }

    /* Refresh Movie List with new Item, so starting from page 1*/
    private void refreshNewMovieList(final String sort_order) {

         /*If sorting order change everything will be reset as per new sorting list*/
        if (isSortingOrderChange) {

                /*Clear Local holding of List, So New List will be populated*/
            mItems.clear();

                /* Reset Sorting Order*/
            isSortingOrderChange = false;

                /* Clear list new list will be added*/
            movieViewAdapter.resetList();

            /* Clear List which is being displayed*/
            populateList(new ArrayList<MovieItem>());
        }

        /* Refresh list with sorting, Only if sorting order is changed by user*/
        if (sort_order.equals(sort_favourite)) {
            Cursor cur = getActivity().getContentResolver().query(MovieContract.FavouriteMovie.CONTENT_URI, null, null, null, null);
            if (cur != null && cur.getCount() > 0) {
                parseCursor(cur);
            } else {
                 /*Remove Empty View*/
                tvEmptyTextView.setVisibility(View.VISIBLE);
                tvEmptyTextView.setText("No Favourite yet");
            }

            /*Work of this method is over so return*/
            return;
        }

        /*For Progress Bar*/
        if (mItems != null)
            mItems.add(null);

        /* By default considering first page*/
        int current_page = 1;
        updateMovieList(sort_order, current_page);
    }


    /*Automatically Adjust about-view*/
    private void networkEmptyView(boolean isConnected) {

        if (isConnected) {
            tvEmptyTextView.setVisibility(View.GONE);
        } else {
                /*Add Empty View*/
            tvEmptyTextView.setVisibility(View.VISIBLE);
            tvEmptyTextView.setText(R.string.msg_internet_status);
        }
    }

    private void updateMovieList(final String sortOrder, final int current_page) {

        new CheckNetworkConnection(getContext(), false, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                networkEmptyView(true);

                if (AppConstant.DEBUG)
                    L.lToast(getContext(), "Connected Inside MainFrag");
                //
                String param[] = new String[]{sortOrder, "2015", String.valueOf(current_page)};
                new FetchMovieList().execute(param);
            }


            @Override
            public void onConnectionFail(String errorMsg) {
                L.lToast(getContext(), getString(R.string.msg_internet_status));

                if (mItems != null && mItems.size() > 0) {
                    // If mItems.contains(null) is true means list is containing progress bar which we dont need so remove it
                    if (mItems.contains(null)) {
                        mItems.remove(mItems.size() - 1);
                        movieViewAdapter.notifyItemRemoved(mItems.size());
                    }
                }

                /*
                *After Removing loader from above bock, Is Size reduces to zero that means nothing to display
                * hence show on Screen No Internet Connection
                * else It would not display and user will be notified only through Toast only
                * */
                if (mItems == null || mItems.size() == 0) {
                    networkEmptyView(false);
                }
            }
        }).execute();

    }

    @Override
    public void onClickMovieThumbnail(View view, int position) {

        ImageView img = (ImageView) view;
        drawable = img.getDrawable();
        referenceForCallback.onItemSelected(mItems.get(position), view);
    }

    class FetchMovieList extends AsyncTask<String, String, List<MovieItem>> {

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

            String value = mItems == null ? "No Value" : mItems.size() + "";
            if (AppConstant.DEBUG_DONE)
                Toast.makeText(getActivity(), mItems.size() + "Before Remove" + value, Toast.LENGTH_SHORT).show();

            if (mItems != null && mItems.size() > 0) {
                //remove progress item
                mItems.remove(mItems.size() - 1);
                movieViewAdapter.notifyItemRemoved(mItems.size());
            }

            if (movieItems != null) {

                /*set Total Pages to scroll*/
                if (movieItems.size() > 0)
                    recycleEndlessScrollListener.setMaxPages(Integer.parseInt(MovieItem.getTotal_pages()));

                if (mItems != null) {
                    if (AppConstant.DEBUG_DONE)
                        Toast.makeText(getActivity(), mItems.size() + "AfterRemove", Toast.LENGTH_SHORT).show();

                    mItems.addAll(movieItems);
                }
            }

             /*Make first item selected first time*/
            if (!isFirstSelected && mItems != null && mItems.size() > 0 && mItems.get(0) != null) {
                isFirstSelected = true;
                Intent in = new Intent(AppConstant.FILTER_OBJECT);
                in.putExtra(AppConstant.OBJECT, mItems.get(0));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(in);

                if (AppConstant.DEBUG_DONE)
                    Toast.makeText(getActivity(), mItems.size() + "Broadcast Sent", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**/
    private void populateList(List<MovieItem> movieItems) {
        movieViewAdapter.addListItem(movieItems);
        movieViewAdapter.notifyDataSetChanged();
    }

    /* Cursor Parsing*/
    private void parseCursor(Cursor cursor) {

        if (cursor != null) {
            List<MovieItem> movieItems = new ArrayList<>();
            cursor.moveToFirst();
            do {
                String oriTitle = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_ORIGINAL_TITLE));
                String serverId = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_MOVIE_SERVER_ID));
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_TITLE));
                String popularity = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_POPULARITY));
                String voteAverage = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_VOTE_AVERAGE));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_RELEASE_DATE));
                String path = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_POSTER_PATH));
                String overView = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_OVERVIEW));

                MovieItem item = new MovieItem(oriTitle, title, popularity, voteAverage);
                item.setPoster_path(path);
                item.setRelease_date(releaseDate);
                item.setServerId(serverId);
                item.setOverview(overView);

                movieItems.add(item);
            } while (cursor.moveToNext());

            /*Reset List Before populating*/
            movieViewAdapter.resetList();
            populateList(movieItems);
        }
    }

    public class NetWorkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.DEBUG)
                Toast.makeText(getActivity(), "NetWork Change", Toast.LENGTH_SHORT).show();

            new CheckNetworkConnection(getActivity(), false, new CheckNetworkConnection.OnConnectionCallback() {
                @Override
                public void onConnectionSuccess() {
                    networkEmptyView(true);

                    //for second time when user press back button we should not load again
                    if (mItems == null || mItems.size() == 0)
                        refreshNewMovieList(sort_order);
                }

                @Override
                public void onConnectionFail(String errorMsg) {
                    networkEmptyView(false);
                }
            }).execute();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*Local Broadcast manager*/
        getActivity().unregisterReceiver(netWorkChangeListener);
    }
}
