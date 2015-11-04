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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import in.udacity.learning.constant.AppConstant;
import in.udacity.learning.dbhelper.MovieContract;
import in.udacity.learning.listener.OnMovieItemClickListener;
import in.udacity.learning.listener.RecycleEndlessScrollListener;
import in.udacity.learning.logger.L;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.network.NetWorkInfoUtility;
import in.udacity.learning.populermovie.app.R;
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

    /*Network Change listente*/
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

    /*Call back Reference*/
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

        netWorkChangeListener = new NetWorkChangeListener();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        /*Local Broadcast manager For network notification*/
        getActivity().registerReceiver(netWorkChangeListener, intentfilter);

        recycleEndlessScrollListener = new RecycleEndlessScrollListener() {
            @Override
            public void onLoadMore(int current_page) {
                //add progress item
                if (mItems != null) {
                    mItems.add(null);
                    if (AppConstant.DEBUG)
                        Toast.makeText(getActivity(), mItems.size() + "Added Loaded", Toast.LENGTH_SHORT).show();
                }

                movieViewAdapter.notifyItemInserted(mItems.size());
                updateMovieList(sort_order, current_page);
            }
        };

        RecyclerView recycleView = (RecyclerView) view.findViewById(R.id.rv_movie_list);
        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recycleView.setLayoutManager(layoutManager);

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
                    break;
                }

                /*If new Order to sort then it is acceptable to call webservice*/
                sort_order = sort_populer;
                break;

            case R.id.action_sort_by_rating:
                if (sort_order.equals(sort_rating)) {
                    isSortingOrderChange = false;
                    break;
                }

                sort_order = sort_rating;
                break;
            case R.id.action_favourite:
                if (sort_order.equals(sort_favourite)) {
                    isSortingOrderChange = false;
                    break;
                }
                sort_order = sort_favourite;
                break;
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(sort_order);
        refreshNewMovieList(sort_order);
        return super.onOptionsItemSelected(item);
    }

    /* Refresh Movie List with new Item, so starting from page 1*/
    private void refreshNewMovieList(String sort_order) {

        /* Refresh list with sorting, Only if sorting order is changed by user*/
        if (sort_order.equals(sort_favourite)) {
            Cursor cur = getActivity().getContentResolver().query(MovieContract.FavouriteMovie.CONTENT_URI, null, null, null, null);
            if (cur != null && cur.getCount() > 0)
                parseCursor(cur);
        } else {

            if (new NetWorkInfoUtility().isNetWorkAvailableNow(getContext())) {
               /*Remove Empty View*/
                tvEmptyTextView.setVisibility(View.GONE);

               /*For Progress Bar*/
                if (mItems != null)
                    mItems.add(null);

            /* By default considering first page*/
                int current_page = 1;
                updateMovieList(sort_order, current_page);
            } else {
                netWorkEmptyView();
            }
        }
    }

    /*Automatically Adjust aboutview*/
    private void netWorkEmptyView() {
        if (new NetWorkInfoUtility().isNetWorkAvailableNow(getContext())) {
            tvEmptyTextView.setVisibility(View.GONE);
        } else {
            /*Add Empty View*/
            tvEmptyTextView.setVisibility(View.VISIBLE);
        }
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
            if (AppConstant.DEBUG)
                Toast.makeText(getActivity(), mItems.size() + "Before Remove", Toast.LENGTH_SHORT).show();

            if (movieItems != null) {

                /*set Total Pages to scroll*/
                if (movieItems.size() > 0)
                    recycleEndlessScrollListener.setMaxPages(Integer.parseInt(MovieItem.getTotal_pages()));

                if (mItems != null) {
                    //remove progress item
                    mItems.remove(mItems.size() - 1);
                    movieViewAdapter.notifyItemRemoved(mItems.size());

                    if (AppConstant.DEBUG)
                        Toast.makeText(getActivity(), mItems.size() + "AfterRemove", Toast.LENGTH_SHORT).show();

                    mItems.addAll(movieItems);
                }
            }

             /*Make first item selected first time*/
            if (!isFirstSelected) {
                isFirstSelected = true;
                Intent in = new Intent(AppConstant.FILTER_OBJECT);
                in.putExtra(AppConstant.OBJECT, mItems.get(0));
                getActivity().sendBroadcast(new Intent(AppConstant.FILTER_OBJECT));
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
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_TITLE));
                String popularity = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_POPULARITY));
                String voteAverage = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_VOTE_AVERAGE));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_RELEASE_DATE));
                String path = cursor.getString(cursor.getColumnIndex(MovieContract.FavouriteMovie.COL_POSTER_PATH));

                MovieItem item = new MovieItem(oriTitle, title, popularity, voteAverage);
                item.setPoster_path(path);
                item.setRelease_date(releaseDate);
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
            Toast.makeText(getActivity(), "NetWork Change", Toast.LENGTH_SHORT).show();

            netWorkEmptyView();

            if (new NetWorkInfoUtility().isNetWorkAvailableNow(getContext())) {
                //for second time when user press back button we should not load again
                if (mItems == null || mItems.size() == 0)
                    refreshNewMovieList(sort_order);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*Local Broadcast manager*/
        getActivity().unregisterReceiver(netWorkChangeListener);
    }


}
