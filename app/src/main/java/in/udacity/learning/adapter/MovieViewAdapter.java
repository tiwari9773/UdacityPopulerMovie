package in.udacity.learning.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.udacity.learning.listener.OnMovieItemClickListener;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;

/**
 * Created by Lokesh on 14-09-2015.
 */
public class MovieViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MovieItem> lsItem;
    private OnMovieItemClickListener onMovieItemClickListener;
    private final int EMPTY_VIEW = 2;
    private final int PROGRESS_VIEW = 1;

    public MovieViewAdapter(List<MovieItem> lsItem, OnMovieItemClickListener onMovieItemClickListener) {
        this.lsItem = lsItem;
        this.onMovieItemClickListener = onMovieItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;
        if (viewType == PROGRESS_VIEW) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_progress_view, viewGroup, false);
            ProgressViewHolder movieHolder = new ProgressViewHolder(view);
            return movieHolder;
        }
        else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie_list_main, viewGroup, false);
            MovieHolder movieHolder = new MovieHolder(view);
            return movieHolder;
        }

        //        else if (viewType == EMPTY_VIEW) {
//            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view, viewGroup, false);
//            EmptyViewHolder evh = new EmptyViewHolder(view);
//            return evh;
//
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof MovieHolder) {
            MovieHolder movieHolder = (MovieHolder) holder;
            movieHolder.tvMovieName.setText(lsItem.get(i).getTitle());

            Glide.with(movieHolder.tvMovieName.getContext())
                    .load(lsItem.get(i).getPoster_path())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(movieHolder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return lsItem.size() == 0 ? 0 : lsItem.size();
    }

    @Override
    public int getItemViewType(int position) {
//        if (lsItem.size() == 0)
//            return EMPTY_VIEW;
        /*null Indicate Loader*/
        if (lsItem.get(position) == null)
            return PROGRESS_VIEW;

        return super.getItemViewType(position);
    }

    /*If New type of element is populated, then reset list*/
    public void resetList() {
        this.lsItem.clear();
    }

    /*Add More Item to existing list*/
    public void addListItem(List<MovieItem> lsItem) {
        this.lsItem.addAll(lsItem);
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvMovieName;

        public MovieHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.iv_movie_thumbnail);
            tvMovieName = (TextView) itemView.findViewById(R.id.tv_movie_name);

            // What would be best way for Recycle Click Listener
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMovieItemClickListener.onClickMovieThumbnail(imageView, getLayoutPosition());
                }
            });
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
