package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 1/12/2018.
 */

public class SearchHistory {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;
    AppCompatActivity appCompatActivity;

    // Shared pref mode ->default values
    int PRIVATE_MODE = 0;
    int Default_null = 0;
    String default_string="null";


    // Sharedpref file name
    private static final String PREF_NAME = "searchhistory";

    // All Shared Preferences Keys
    //Registration information
    public static final String KEY_LATITUDE            = "latitude";
    public static final String KEY_LONGITUDE           = "longitude";

    public SearchHistory(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //save coordinates
    public void setKeyLatitudeLongitude(String latitude,String longitude) {
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(KEY_LONGITUDE, longitude);
        // commit changes
        editor.commit();
    }

    //get coordinates
    public  String getKeyLatitude(){
        return pref.getString(KEY_LATITUDE,null);
    }

    public  String getKeyLongitude(){
        return pref.getString(KEY_LONGITUDE,null);
    }

}
