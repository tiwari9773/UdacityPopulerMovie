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
import in.udacity.learning.listener.OnTrailerClickListener;
import in.udacity.learning.model.MovieItem;
import in.udacity.learning.model.TrailerItem;
import in.udacity.learning.populermovie.app.R;

/**
 * Created by Lokesh on 14-09-2015.
 */
public class TrailerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TrailerItem> lsItem;
    private OnTrailerClickListener onTrailerClickListener;
    private final int EMPTY_VIEW = -1;

    public TrailerViewAdapter(List<TrailerItem> lsItem, OnTrailerClickListener onTrailerClickListener) {
        this.lsItem = lsItem;
        this.onTrailerClickListener = onTrailerClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;
        if (viewType == EMPTY_VIEW) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view, viewGroup, false);
            EmptyViewHolder evh = new EmptyViewHolder(view);
            return evh;

        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_trailer, viewGroup, false);
            ItemHolder movieHolder = new ItemHolder(view);
            return movieHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            Glide.with(itemHolder.imageView.getContext())
                    .load(lsItem.get(i).getTrailerPath())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(itemHolder.imageView);

            itemHolder.textView.setText(lsItem.get(i).getName());
        }

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

    class ItemHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ItemHolder(final View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.iv_trailer);
            textView = (TextView) itemView.findViewById(R.id.tv_trailerName);

            // What would be best way for Recycle Click Listener
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTrailerClickListener.onMovieTrailer(lsItem.get(getLayoutPosition()).getKey());
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
