package in.udacity.learning.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Lokesh on 24-10-2015.
 */
public abstract class RecycleEndlessScrollListener extends RecyclerView.OnScrollListener {

    private String TAG = RecycleEndlessScrollListener.class.getName();

    /*Express Current Visible Position in List*/
    private int firstVisibleItemPosition;

    /* Handles total of item in list*/
    private int totalItemCount;

    /* Holds total visible item on screen at any point*/
    private int totalVisibleItem;

    // True if we are still waiting for the last set of data to load.
    private boolean isLoading = false;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 8;

    // Page define each iteration download , It should be always less than total pages
    private int currentPage = 1;

    // Set Total no of pages requred to be loaded
    private int maxPages = 3;

    public int getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    /* Layout manager holds all information of list visible position*/
    private GridLayoutManager gridLayoutManager;

    public RecycleEndlessScrollListener() {
    }

    public RecycleEndlessScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (gridLayoutManager == null)
            gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        if (gridLayoutManager != null) {
            totalItemCount = gridLayoutManager.getItemCount();
            totalVisibleItem = gridLayoutManager.getChildCount();
            firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
        }

        if (currentPage < getMaxPages()) {
        /* Now total is increased so we can reset flag*/
            if (isLoading && firstVisibleItemPosition + totalVisibleItem + visibleThreshold < totalItemCount) {
                isLoading = false;
            }

         /* Now total is less so we can demand for load*/
            if (!isLoading && firstVisibleItemPosition + totalVisibleItem + visibleThreshold > totalItemCount) {
                currentPage++;
                isLoading = true;
                onLoadMore(currentPage);
            }
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    public abstract void onLoadMore(int current_page);

}
