package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 1/24/2018.
 */

public class FcmDeviceKey {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared pref mode ->default values
    int PRIVATE_MODE = 0;
    int Default_null = 0;
    String default_string="null";


    // Sharedpref file name
    private static final String PREF_NAME = "fcmdevicekey";

    // Sharedpref key
    public static final String KEY_FCMDEVICEKEY = "fcmkey";

    public FcmDeviceKey(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //save key
    public void setKeyFcmdevicekey(String fcmdevicekey) {
        editor.putString(KEY_FCMDEVICEKEY, fcmdevicekey);
        // commit changes
        editor.commit();
    }

    //get key
    public  String getKeyFcmdevicekey(){
        return pref.getString(KEY_FCMDEVICEKEY,"");
    }

}
