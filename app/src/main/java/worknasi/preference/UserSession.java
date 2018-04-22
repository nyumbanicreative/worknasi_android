package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 1/4/2018.
 */

public class UserSession {

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
    private static final String PREF_NAME = "usersession";

    // All Shared Preferences Keys
    //Registration information
    public static final String KEY_USER_ID            = "userid";
    public static final String KEY_USER_NAME          = "username";
    public static final String KEY_FIRSTNAME          = "firstname";
    public static final String KEY_LASTNAME           = "lastname";
    public static final String KEY_USEREMAIL          = "email";
    public static final String KEY_USERPICTURE        = "picture";
    public static final String KEY_USERKEY            = "key";
    public static final String KEY_USERABOUT          = "about";
    public static final String KEY_USERTYPE           = "type";

    //Account state [user sign in] [user sign out]
    private static final String IS_LOGIN = "IsLoggedIn";

    // Constructor
    public UserSession(Context context,AppCompatActivity appCompatActivity){
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public UserSession(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //save session
    public void setUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        // commit changes
        editor.commit();
    }

    public void setUserName(String userName){
        editor.putString(KEY_USER_NAME, userName);
        // commit changes
        editor.commit();
    }

    public void setUserFirstName(String firstName){
        editor.putString(KEY_FIRSTNAME, firstName);
        // commit changes
        editor.commit();
    }

    public void setUserLastName(String secondName){
        editor.putString(KEY_LASTNAME, secondName);
        // commit changes
        editor.commit();
    }

    public void setUserEmail(String email){
        editor.putString(KEY_USEREMAIL, email);
        // commit changes
        editor.commit();
    }

    public void setUserPicture(String picture){
        editor.putString(KEY_USERPICTURE, picture);
        // commit changes
        editor.commit();
    }

    public void setUserKey(String key){
        editor.putString(KEY_USERKEY, key);
        // commit changes
        editor.commit();
    }


    public void setUserAbout(String about){
        editor.putString(KEY_USERABOUT, about);
        // commit changes
        editor.commit();
    }


    public void setUserType(String type){
        editor.putString(KEY_USERTYPE, type);
        // commit changes
        editor.commit();
    }

    /**
     * Get session
     * */

    public  String getUserId(){
        return pref.getString(KEY_USER_ID,"0");
    }

    public  String getUserName() {
        return pref.getString(KEY_USER_NAME,null);
    }

    public  String getFirstName() {
        return pref.getString(KEY_FIRSTNAME,null);
    }

    public  String getLastName() {
        return pref.getString(KEY_LASTNAME,null);
    }

    public  String getUserEmail() {
        return pref.getString(KEY_USEREMAIL,null);
    }

    public  String getUserPicture() {
        return pref.getString(KEY_USERPICTURE,"");
    }

    public  String getUserKey() {
        return pref.getString(KEY_USERKEY,null);
    }

    public  String getUserAbout()    {
        return pref.getString(KEY_USERABOUT,null);
    }

    public  String getKeyUsertype()    {
        return pref.getString(KEY_USERTYPE,null);
    }




    //sign in
    public void LoginUser(){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // commit changes
        editor.commit();
    }

    //check login
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Clear session details
     * */

    public void clearSession(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

}
