package in.udacity.learning.web_service;

/**
 * Created by Lokesh on 06-09-2015.
 */
public interface WebServiceParsingKeys {

    interface MovieKeys {
        String RESULTS = "results";
        String PAGE = "page";
        String ADULT = "adult";
        String BACKDROP_PATH = "backdrop_path";
        String GENRE_IDS = "genre_ids";
        String ID = "id";
        String ORIGINAL_LANGUAGE = "original_language";
        String ORIGINAL_TITLE = "original_title";
        String OVERVIEW = "overview";
        String RELEASE_DATE = "release_date";
        String POSTER_PATH = "poster_path";
        String TITLE = "title";
        String VIDEO = "video";
        String POPULARITY = "popularity";
        String VOTE_AVERAGE = "vote_average";
        String VOTE_COUNT = "vote_count";
        String TOTAL_PAGES = "total_pages";
        String TOTAL_RESULTS = "total_results";
    }

    interface TrailerKeys {
        String RESULTS ="results";
        String ID ="id";
        String ISO_639_1 = "iso_639_1";
        String KEY ="key";
        String NAME ="name";
        String SITE ="site";
        String SIZE ="size";
        String TYPE ="type";
    }

    interface ReviewKeys {
        String RESULTS ="results";
        String ID ="id";
        String AUTHOR = "author";
        String CONTENT ="content";
        String URI ="url";
    }
}
