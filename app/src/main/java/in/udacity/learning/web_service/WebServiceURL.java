package in.udacity.learning.web_service;

/**
 * Created by USER on 03-Sep-15.
 */
public class WebServiceURL {


    public static String baseURL = "https://api.themoviedb.org/3/discover/movie?";
    public static String baseURLThumbnail = "http://image.tmdb.org/t/p/w154/";
    public static String baseURLPoster = "http://image.tmdb.org/t/p/w185/";

    ///discover/movie?primary_release_date.gte=2014-09-15&primary_release_date.lte=2014-10-22
    //discover/movie?sort_by=popularity.desc
    //discover/movie/?certification_country=US&certification=R&sort_by=vote_average.desc

    public static String API_KEY = "api_key";
    public static String YEAR = "year";
    public static String SORT_BY = "sort_by";

    public static String PRIMARY_RELEASE_YEAR = "primary_release_year";
    public static String PRIMARY_RELEASE_DATE_GTE = "primary_release_date.gte";
    public static String PRIMARY_RELEASE_DATE_LTE = "primary_release_date.lte";
    public static String CERTIFICATION_COUNTRY = "certification_country";
    public static String CERTIFICATION = "certification";


    //public static String MODE = "mode";
//    "backdrop_sizes": [ "w300","w780", "w1280", "original" ],
//    "logo_sizes": [ "w45", "w92", "w154", "w185", "w300", "w500", "original" ],
//    "poster_sizes": [ "w92", "w154", "w185", "w342", "w500", "w780", "original" ],
//    "profile_sizes": [ "w45", "w185", "h632", "original" ],
//    "still_sizes": [ "w92", "w185", "w300", "original" ]


}
