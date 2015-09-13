package in.udacity.learning.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.udacity.learning.adapter.MovieViewAdapter;
import in.udacity.learning.framework.OnMovieItemClickListener;
import in.udacity.learning.logger.L;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.network.NetWorkInfoUtility;
import in.udacity.learning.populermovie.app.R;
import in.udacity.learning.serviceutility.HttpURLConnectionInfo;
import in.udacity.learning.serviceutility.JSONParser;

/**
 * Created by Lokesh on 13-09-2015.
 */
public class FragmentMain extends Fragment implements OnMovieItemClickListener{
    private String TAG = getClass().getName();
    private MovieViewAdapter movieViewAdapter;
    private List<MovieItem> mItem = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initialise(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMovieList();
    }

    private void updateMovieList() {
        String param[] = new String[]{"discover", "2015", "popularity.desc"};

        if (new NetWorkInfoUtility().isNetWorkAvailableNow(getContext())) {
            new FetchMovieList().execute(param);
        } else {
            L.lToast(getContext(), getString(R.string.msg_internet_status));
        }
    }

    private void initialise(View view) {
        RecyclerView recycleView = (RecyclerView) view.findViewById(R.id.rv_movie_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recycleView.setLayoutManager(gridLayoutManager);

        movieViewAdapter = new MovieViewAdapter(mItem,this);
        recycleView.setAdapter(movieViewAdapter);
    }

    @Override
    public void onClickMoviePoster(int position) {

    }

    class FetchMovieList extends AsyncTask<String, String, List<MovieItem>> {

        @Override
        protected List<MovieItem> doInBackground(String... params) {

            String mode = params[0];
            String year = params[1];
            String sort_by = params[2];
            String jsonString = new HttpURLConnectionInfo(mode, sort_by, Integer.parseInt(year)).getJSON(TAG);
            List<MovieItem> movieItems = JSONParser.parseMovieList(jsonString);
            return movieItems;
        }

        @Override
        protected void onPostExecute(List<MovieItem> movieItems) {
            super.onPostExecute(movieItems);

            populateList(movieItems);
        }
    }

    private void populateList(List<MovieItem> movieItems) {
        mItem = movieItems;
        movieViewAdapter.setLsItem(mItem);
        movieViewAdapter.notifyDataSetChanged();
    }
}
