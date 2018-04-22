package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2/6/2018.
 */

public class Favoritepreference {

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
    private static final String PREF_NAME = "favoritepreference";

    // All Shared Preferences Keys

    //Country information
    public static final String KEY_USER_ID         = "user_id";
    public static final String KEY_PROPERTY_ID     = "property_id";
    public static final String KEY_FAVORITE        = "favorite";

    public Favoritepreference(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void addFavoritePreference(String user_id,String property_id,String favorite){
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_PROPERTY_ID, property_id);
        editor.putString(KEY_FAVORITE, favorite);
        // commit changes
        editor.commit();
    }

    /*
    get codes information
     */

    public  String getKeyUserId(int position) {
        String[] user_id = pref.getString(KEY_USER_ID,"").split("-");
        return user_id[position];
    }

    public  String getKeyUserId() {
        return pref.getString(KEY_USER_ID,"");
    }

    public  String getKeyPropertyId(int position) {
        String[] property_id = pref.getString(KEY_PROPERTY_ID,"").split("-");
        return property_id[position];
    }

    public  String getKeyPropertyId() {
        return pref.getString(KEY_PROPERTY_ID,"");
    }

    public  String getKeyFavorite(int position) {
        String[] favorite = pref.getString(KEY_FAVORITE,"").split("-");
        return favorite[position];
    }

    public  String getKeyFavorite() {
        return pref.getString(KEY_FAVORITE,"");
    }

    public  int getKeyitemSize() {
        return  pref.getString(KEY_USER_ID,"").split("-").length;
    }

}
