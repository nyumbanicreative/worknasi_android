package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 1/2/2018.
 */

public class ArrayProperties {

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
    private static final String PREF_NAME = "arrayproperties";

    // All Shared Preferences Keys
    //Country information
    public static final String KEY_PHOTO_URL          = "photourl";

    public static final String KEY_AMENITIES_LIST     = "amenity";

    public static final String KEY_CHARGES_LIST       = "charges";

    public static final String KEY_REASON_LIST        = "resaons";

    public static final String KEY_RULES_LIST         = "rules";

    public ArrayProperties(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //save photo information
    public void setKeyPhotoUrl(String photoUrl) {
        editor.putString(KEY_PHOTO_URL, photoUrl);
        // commit changes
        editor.commit();
    }


    //save amenities information
    public void setKeyAmenitiesList(String amenities) {
        editor.putString(KEY_AMENITIES_LIST, amenities);
        // commit changes
        editor.commit();
    }

    //save charges information
    public void setKeyChargesList(String charges) {
        editor.putString(KEY_CHARGES_LIST, charges);
        // commit changes
        editor.commit();
    }

    //save reasons information
    public void setKeyReasonList(String reasons) {
        editor.putString(KEY_REASON_LIST, reasons);
        // commit changes
        editor.commit();
    }

    //save rules information
    public void setKeyRulesList(String rules) {
        editor.putString(KEY_RULES_LIST, rules);
        // commit changes
        editor.commit();
    }

    //get
    public  String getKeyPhotoUrl(){
        return pref.getString(KEY_PHOTO_URL,"");
    }

    public  String getKeyAmenitiesList(){
        return pref.getString(KEY_AMENITIES_LIST,"");
    }

    public  String getKeyChargesList(){
        return pref.getString(KEY_CHARGES_LIST,"");
    }

    public  String getKeyReasonList(){
        return pref.getString(KEY_REASON_LIST,"");
    }

    public  String getKeyRulesList(){
        return pref.getString(KEY_RULES_LIST,"");
    }
}
