package in.udacity.learning.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.udacity.learning.serviceutility.WebServiceURL;
import in.udacity.learning.framework.OnMovieItemClickListener;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;

/**
 * Created by Lokesh on 14-09-2015.
 */
public class MovieViewAdapter extends RecyclerView.Adapter<MovieViewAdapter.MovieHolder> {

    private List<MovieItem> lsItem;
    private OnMovieItemClickListener onMovieItemClickListener;

    public MovieViewAdapter(List<MovieItem> lsItem, OnMovieItemClickListener onMovieItemClickListener) {
        this.lsItem = lsItem;
        this.onMovieItemClickListener = onMovieItemClickListener;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie_list_main, viewGroup, false);
        MovieHolder movieHolder = new MovieHolder(view);

        return movieHolder;
    }

    @Override
    public void onBindViewHolder(MovieHolder movieHolder, int i) {
        movieHolder.textView.setText(lsItem.get(i).getTitle());


        Glide.with(movieHolder.textView.getContext())
                .load(WebServiceURL.baseURLPoster+"/"+lsItem.get(i).getPoster_path())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(movieHolder.imageView);


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
        return lsItem.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView textView;

        public MovieHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            textView = (TextView) itemView.findViewById(R.id.tv_movie_name);
        }
    }

    public void setLsItem(List<MovieItem> lsItem) {
        this.lsItem = lsItem;
    }

}
