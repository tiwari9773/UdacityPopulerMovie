package in.udacity.learning.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.udacity.learning.web_service.WebServiceURL;
import in.udacity.learning.framework.OnMovieItemClickListener;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;

/**
 * Created by Lokesh on 14-09-2015.
 */
public class MovieViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MovieItem> lsItem;
    private OnMovieItemClickListener onMovieItemClickListener;
    private final int EMPTY_VIEW = -1;

    public MovieViewAdapter(List<MovieItem> lsItem, OnMovieItemClickListener onMovieItemClickListener) {
        this.lsItem = lsItem;
        this.onMovieItemClickListener = onMovieItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;
        if (viewType == EMPTY_VIEW) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view, viewGroup, false);
            EmptyViewHolder evh = new EmptyViewHolder(view);
            return evh;

        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie_list_main, viewGroup, false);
            MovieHolder movieHolder = new MovieHolder(view);
            return movieHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof MovieHolder) {
            MovieHolder movieHolder = (MovieHolder) holder;
            movieHolder.tvMovieName.setText(lsItem.get(i).getTitle());
            movieHolder.tvPopularity.setText("Popularity = " + lsItem.get(i).getPopularity());
            movieHolder.tvVoting.setText("Average Vote = " + lsItem.get(i).getVote_average());
            Glide.with(movieHolder.tvMovieName.getContext())
                    .load(WebServiceURL.baseURLThumbnail + "/" + lsItem.get(i).getPoster_path())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(movieHolder.imageView);
        }

//        AnimatorSet animatorSet = new AnimatorSet();
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(movieHolder.itemView, "scaleY", 1f, 1.5f);
//        ObjectAnimator scaleX = ObjectAnimator.ofFloat(movieHolder.itemView, "scaleX", 1f, 1.5f);
//        scaleY.setInterpolator(new DecelerateInterpolator());
//        scaleX.setInterpolator(new DecelerateInterpolator());
//
//        animatorSet.playTogether(scaleY,scaleX);
//        animatorSet.setDuration(700);
//        animatorSet.start();

    }

    @Override
    public int getItemCount() {
        return lsItem.size() == 0 ? 1 : lsItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (lsItem.size() == 0)
            return EMPTY_VIEW;

        return super.getItemViewType(position);
    }

    public void setLsItem(List<MovieItem> lsItem) {
        this.lsItem = lsItem;
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvMovieName;
        TextView tvPopularity;
        TextView tvVoting;


        public MovieHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.iv_movie_thumbnail);
            tvMovieName = (TextView) itemView.findViewById(R.id.tv_movie_name);
            tvPopularity = (TextView) itemView.findViewById(R.id.tv_popularity);
            tvVoting = (TextView) itemView.findViewById(R.id.tv_vote);

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


}
