package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 1/26/2018.
 */

public class CreateChat {

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
    private static final String PREF_NAME = "creatchat";

    // Sharedpref key
    public static final String KEY_TARGET_ID = "target_id";
    public static final String KEY_TARGET_NAME = "target_name";
    public static final String KEY_TARGET_IMAGE = "target_image";

    public CreateChat(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //save key
    public void setKeyTargetId(String targetId) {
        editor.putString(KEY_TARGET_ID, targetId);
        // commit changes
        editor.commit();
    }

    public void setKeyTargetName(String targetName) {
        editor.putString(KEY_TARGET_NAME, targetName);
        // commit changes
        editor.commit();
    }

    public void setKeyTargetImage(String targetImage) {
        editor.putString(KEY_TARGET_IMAGE, targetImage);
        // commit changes
        editor.commit();
    }

    //get key
    public  String getKeyTargetId(){
        return pref.getString(KEY_TARGET_ID,"");
    }

    public  String getKeyTargetName(){
        return pref.getString(KEY_TARGET_NAME,"");
    }

    public  String getKeyTargetImage(){
        return pref.getString(KEY_TARGET_IMAGE,"");
    }
}
