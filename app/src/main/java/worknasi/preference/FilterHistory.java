package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 12/20/2017.
 */

public class FilterHistory {

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
    private static final String PREF_NAME = "filterhistory";

    // All Shared Preferences Keys

    //Registration information
    public static final String KEY_COUNTRY_NAME        = "country_name";
    public static final String KEY_COUNTRY_ID          = "country_id";
    public static final String KEY_STATE_NAME          = "state_name";
    public static final String KEY_STATE_ID            = "state_id";
    public static final String KEY_CITY_NAME           = "city_name";
    public static final String KEY_CITY_ID             = "city_id";
    public static final String KEY_DATE                = "date";

    //Account state [user sign in] [user sign out]
    private static final String IS_SAVED = "IsSaved";

    // Constructor
    public FilterHistory(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setKeyCountryName(String countryName) {
        editor.putString(KEY_COUNTRY_NAME,countryName);
        // commit changes
        editor.commit();
    }

    //save registration
    public void setKeyCountryId(String countryId) {
        editor.putString(KEY_COUNTRY_ID,countryId);
        // commit changes
        editor.commit();
    }

    public void setKeyStateName(String stateName) {
        editor.putString(KEY_STATE_NAME,stateName);
        // commit changes
        editor.commit();
    }

    public void setKeyStateId(String stateId) {
        editor.putString(KEY_STATE_ID,stateId);
        // commit changes
        editor.commit();
    }

    public void setKeyCityName(String cityName) {
        editor.putString(KEY_CITY_NAME,cityName);
        // commit changes
        editor.commit();
    }

    public void setKeyCityId(String cityId) {
        editor.putString(KEY_CITY_ID,cityId);
        // commit changes
        editor.commit();
    }

    public void setKeyDate(String date) {
        editor.putString(KEY_DATE,date);
        // commit changes
        editor.commit();
    }



    /**
     * Get history
     * */

    public  String getKeyCountryName(){
        return pref.getString(KEY_COUNTRY_NAME,null);
    }

    public  String getKeyCountryId(){
        return pref.getString(KEY_COUNTRY_ID,null);
    }

    public  String getKeyStateName(){
        return pref.getString(KEY_STATE_NAME,null);
    }

    public  String getKeyStateId(){
        return pref.getString(KEY_STATE_ID,null);
    }

    public  String getKeyCityName(){
        return pref.getString(KEY_CITY_NAME,null);
    }

    public  String getKeyCityId(){
        return pref.getString(KEY_CITY_ID,null);
    }

    public  String getKeyDate(){
        return pref.getString(KEY_DATE,null);
    }


    /*
    History state
     */

    //save history state
    public void SaveHistoryState(){
        // Storing login value as TRUE
        editor.putBoolean(IS_SAVED, true);
        // commit changes
        editor.commit();
    }

    //check login
    public boolean isSaved(){
        return pref.getBoolean(IS_SAVED, false);
    }

    /**
     *Clear history details
     **/

    public void clearSession(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }


}
