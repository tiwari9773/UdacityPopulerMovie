package in.udacity.learning.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lokesh on 17-09-2015.
 */
public class Utility {

    /**
     * get Currunt date in 2014-10-22 format
     */
    public static String getTodaysDate(Date date) {
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return strDate;
    }

    /**
     * get Currunt date in 2014-10-22 format
     */
    public static String getTodaysDate(Calendar date) {
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return strDate;
    }
}
