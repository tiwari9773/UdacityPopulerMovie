package in.udacity.learning.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lokesh on 14-09-2015.
 */
public class MovieItem implements Parcelable {

    private String popularity = "popularity";
    private String page = "page";
    private String adult = "adult";
    private String backdrop_path = "backdrop_path";
    private String genre_ids = "genre_ids";

    private String id = "id";
    private String serverId = "serverId";

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    private String original_language = "original_language";
    private String original_title = "original_title";
    private String overview = "overview";
    private String release_date = "release_date";
    private String poster_path = "poster_path";
    private String title = "title";
    private String video = "video";
    private String vote_average = "vote_average";
    private String vote_count = "vote_count";
    private static String total_pages = "total_pages";
    private static String total_results = "total_results";

    public MovieItem(String title, String poster_path, String popularity, String vote_avarge) {
        this.title = title;
        this.poster_path = poster_path;
        this.popularity = popularity;
        this.vote_average = vote_avarge;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public static String getTotal_pages() {
        return total_pages;
    }

    public static void setTotal_pages(String total_pages) {
        MovieItem.total_pages = total_pages;
    }

    public static String getTotal_results() {
        return total_results;
    }

    public static void setTotal_results(String total_results) {
        MovieItem.total_results = total_results;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    protected MovieItem(Parcel in) {
        popularity = in.readString();
        serverId = in.readString();
        page = in.readString();
        adult = in.readString();
        backdrop_path = in.readString();
        genre_ids = in.readString();
        id = in.readString();
        original_language = in.readString();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        title = in.readString();
        video = in.readString();
        vote_average = in.readString();
        vote_count = in.readString();
        total_pages = in.readString();
        total_results = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(popularity);
        dest.writeString(serverId);
        dest.writeString(page);
        dest.writeString(adult);
        dest.writeString(backdrop_path);
        dest.writeString(genre_ids);
        dest.writeString(id);
        dest.writeString(original_language);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(title);
        dest.writeString(video);
        dest.writeString(vote_average);
        dest.writeString(vote_count);
        dest.writeString(total_pages);
        dest.writeString(total_results);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}