package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 1/25/2018.
 */

public class CreateRent {

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
    private static final String PREF_NAME = "createrent";

    // Sharedpref key
    public static final String KEY_PROPERTY_ID = "property_id";
    public static final String KEY_PROPERTY_NAME = "property_name";
    public static final String KEY_PLAN_ID = "plan_id";
    public static final String KEY_PLAN_DESCRIPTION = "plan_description";

    public CreateRent(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //save key
    public void setKeyPropertyId(String propertyId) {
        editor.putString(KEY_PROPERTY_ID, propertyId);
        // commit changes
        editor.commit();
    }

    public void setKeyPropertyName(String propertyName) {
        editor.putString(KEY_PROPERTY_NAME, propertyName);
        // commit changes
        editor.commit();
    }

    public void setKeyPlanId(String planId) {
        editor.putString(KEY_PLAN_ID, planId);
        // commit changes
        editor.commit();
    }

    public void setKeyPlanDescription(String planDescription) {
        editor.putString(KEY_PLAN_DESCRIPTION, planDescription);
        // commit changes
        editor.commit();
    }

    //get key
    public  String getKeyPropertyId(){
        return pref.getString(KEY_PROPERTY_ID,"");
    }

    public  String getKeyPropertyName(){
        return pref.getString(KEY_PROPERTY_NAME,"");
    }

    public  String getKeyPlanId(){
        return pref.getString(KEY_PLAN_ID,"");
    }

    public  String getKeyPlanDescription(){
        return pref.getString(KEY_PLAN_DESCRIPTION,"");
    }

}
