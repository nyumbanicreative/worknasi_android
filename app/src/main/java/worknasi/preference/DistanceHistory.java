package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 1/13/2018.
 */

public class DistanceHistory {

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
    String default_distance = "20";


    // Sharedpref file name
    private static final String PREF_NAME = "distancefilter";

    // All Shared Preferences Keys

    //Registration information
    public static final String KEY_DISTANCE        = "distance";

    // Constructor
    public DistanceHistory(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDistance(String distance) {
        editor.putString(KEY_DISTANCE,distance);
        // commit changes
        editor.commit();
    }

    /**
     * Get history
     * */
    public  String getDistance(){
        return pref.getString(KEY_DISTANCE,default_distance);
    }

}
