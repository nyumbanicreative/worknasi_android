package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 1/1/2018.
 */

public class PreviewData {

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
    private static final String PREF_NAME = "previewdata";

    // All Shared Preferences Keys
    public static final String KEY_USER_ID            = "user_id";
    public static final String KEY_FIRSTNAME          = "firstname";
    public static final String KEY_LASTNAME           = "lastname";
    public static final String KEY_EMAIL              = "email";
    public static final String KEY_ABOUT              = "about";
    public static final String KEY_DP                 = "dp";

    //Country information
    public static final String KEY_PHOTO_URL          = "photourl";
    public static final String KEY_PHOTOLIST_SIZE     = "photourlsize";

    public static final String KEY_AMENITIES_LIST     = "amenity";
    public static final String KEY_AMENITIES_SIZE     = "amenitysize";

    public static final String KEY_CHARGES_LIST       = "charges";
    public static final String KEY_CHARGES_SIZE       = "chargessize";

    public static final String KEY_REASON_LIST        = "resaons";
    public static final String KEY_REASON_SIZE        = "reasonssize";

    public static final String KEY_RULES_LIST         = "rules";
    public static final String KEY_RULES_SIZE         = "rulessize";

    public PreviewData(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    //save user
    public void setKeyUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        // commit changes
        editor.commit();
    }

    public void setKeyFirstname(String firstname) {
        editor.putString(KEY_FIRSTNAME, firstname);
        // commit changes
        editor.commit();
    }

    public void setKeyLastname(String lastname) {
        editor.putString(KEY_LASTNAME, lastname);
        // commit changes
        editor.commit();
    }

    public void setKeyEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        // commit changes
        editor.commit();
    }

    public void setKeyAbout(String about) {
        editor.putString(KEY_ABOUT, about);
        // commit changes
        editor.commit();
    }

    public void setKeyDp(String dp) {
        editor.putString(KEY_DP, dp);
        // commit changes
        editor.commit();
    }


    //save photo information
    public void setKeyPhotoUrl(String photoUrl) {
        editor.putString(KEY_PHOTO_URL, photoUrl);
        // commit changes
        editor.commit();
    }

    public void setKeyPhotoListSize(String size) {
        editor.putString(KEY_PHOTOLIST_SIZE, size);
        // commit changes
        editor.commit();
    }


    //save amenities information
    public void setKeyAmenitiesList(String amenities) {
        editor.putString(KEY_AMENITIES_LIST, amenities);
        // commit changes
        editor.commit();
    }

    public void setKeyAmenitiesSize(String size) {
        editor.putString(KEY_AMENITIES_SIZE, size);
        // commit changes
        editor.commit();
    }

    //save charges information
    public void setKeyChargesList(String charges) {
        editor.putString(KEY_CHARGES_LIST, charges);
        // commit changes
        editor.commit();
    }

    public void setKeyChargesSize(String size) {
        editor.putString(KEY_CHARGES_SIZE, size);
        // commit changes
        editor.commit();
    }

    //save reasons information
    public void setKeyReasonList(String reasons) {
        editor.putString(KEY_REASON_LIST, reasons);
        // commit changes
        editor.commit();
    }

    public void setKeyReasonSize(String size) {
        editor.putString(KEY_REASON_SIZE, size);
        // commit changes
        editor.commit();
    }

    //save rules information
    public void setKeyRulesList(String rules) {
        editor.putString(KEY_RULES_LIST, rules);
        // commit changes
        editor.commit();
    }

    public void setKeyRulesSize(String size) {
        editor.putString(KEY_RULES_SIZE, size);
        // commit changes
        editor.commit();
    }


    //get user information
    public  String getKeyUserId() {
        return pref.getString(KEY_USER_ID,null);
    }

    public  String getKeyFirstname() {
        return pref.getString(KEY_FIRSTNAME,null);
    }

    public  String getKeyLastname() {
        return pref.getString(KEY_LASTNAME,null);
    }

    public  String getKeyEmail() {
        return pref.getString(KEY_EMAIL,null);
    }

    public  String getKeyAbout() {
        return pref.getString(KEY_ABOUT,null);
    }

    public  String getKeyDp() {
        return pref.getString(KEY_DP,null);
    }


    //get photo information
    public  String getKeyPhotoUrl(int position) {
        String[] photourl_array = pref.getString(KEY_PHOTO_URL,null).split("-");
        return photourl_array[position];
    }

    public  String getKeyPhotolistSize() {
        return pref.getString(KEY_PHOTOLIST_SIZE,null);
    }

    //get amenities information
    public  String getKeyAmenitiesList(int position) {
        String[] amenities_array = pref.getString(KEY_AMENITIES_LIST,null).split("-");
        return amenities_array[position];
    }

    public  String getKeyAmenitiesSize() {
        return pref.getString(KEY_AMENITIES_SIZE,null);
    }


    //get charges information
    public  String getKeyChargesList(int position) {
        String[] charges_array = pref.getString(KEY_CHARGES_LIST,null).split("-");
        return charges_array[position];
    }

    public  String getKeyChargesSize() {
        return pref.getString(KEY_CHARGES_SIZE,null);
    }


    //get reasons information
    public  String getKeyReasonList(int position) {
        String[] reason_array = pref.getString(KEY_REASON_LIST,null).split("-");
        return reason_array[position];
    }

    public  String getKeyReasonSize() {
        return pref.getString(KEY_REASON_SIZE,null);
    }


    //get rules information
    public  String getKeyRulesList(int position) {
        String[] rules_array = pref.getString(KEY_RULES_LIST,null).split("-");
        return rules_array[position];
    }

    public  String getKeyRulesSize() {
        return pref.getString(KEY_RULES_SIZE,null);
    }


}
