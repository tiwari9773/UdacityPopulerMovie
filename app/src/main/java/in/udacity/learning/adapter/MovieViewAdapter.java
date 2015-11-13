package in.udacity.learning.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import in.udacity.learning.constant.AppConstant;
import in.udacity.learning.listener.OnMovieItemClickListener;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.populermovie.app.R;

/**
 * Created by Lokesh on 14-09-2015.
 */
public class MovieViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "MovieViewAdapter";
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
        } else {
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
            final MovieHolder movieHolder = (MovieHolder) holder;
            movieHolder.tvMovieName.setText(lsItem.get(i).getTitle());

            Glide.with(movieHolder.tvMovieName.getContext())
                    .load(lsItem.get(i).getPoster_path()).asBitmap()
                    .listener(
                            new RequestListener<String, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    Palette.from(resource).generate(
                                            new Palette.PaletteAsyncListener() {
                                                @Override
                                                public void onGenerated(Palette palette) {
                                                    int transparent = R.color.tranparent;
                                                    int extractedColor  = palette.getDarkMutedColor(transparent);

                                                    movieHolder.tvMovieName.setBackgroundColor(adjustAlpha(extractedColor,0.8f));
                                                }
                                            }

                                    );
                                    return false;
                                }
                            }
                    )
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(movieHolder.imageView);
        }

    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
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
            itemView.setOnClickListener(new View.OnClickListener() {
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
