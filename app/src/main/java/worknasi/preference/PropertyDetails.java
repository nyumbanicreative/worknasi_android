package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 1/1/2018.
 */

public class PropertyDetails {

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
    private static final String PREF_NAME = "propertydetails";

    // All Shared Preferences Keys

    //Registration information
    public static final String KEY_PROPERTY_ID        = "property_id";
    public static final String KEY_PROPERTY_NAME      = "property_name";
    public static final String KEY_DESCRIPTION        = "description";
    public static final String KEY_PROPERTY_TYPE      = "property_type";
    public static final String KEY_OFFICE_TYPE        = "office_type";
    public static final String KEY_LATITUDE           = "latitude";
    public static final String KEY_LONGITUDE          = "longitude";
    public static final String KEY_ADDRESS            = "address";
    public static final String KEY_COUNTRY_NAME       = "country_name";
    public static final String KEY_STATE_NAME         = "state_name";
    public static final String KEY_CITY_NAME          = "city_name";
    public static final String KEY_CURRENCY_NAME      = "currency_name";
    public static final String KEY_REFUND_TYPE        = "refund_type";
    public static final String KEY_SPACE_ACCOMODATES  = "space_accomodates_count";
    public static final String KEY_PRICE              = "price";
    public static final String KEY_IMAGE              = "image";
    public static final String KEY_DISTANCE           = "distance";
    public static final String KEY_FAVOURED           = "favoured";
    public static final String KEY_RATINGS            = "ratings";

    // Constructor
    public PropertyDetails(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setKeyPropertyId(String propertyId) {
        editor.putString(KEY_PROPERTY_ID,propertyId);
        // commit changes
        editor.commit();
    }

    public void setKeyPropertyName(String propertyName) {
        editor.putString(KEY_PROPERTY_NAME,propertyName);
        // commit changes
        editor.commit();
    }

    public void setKeyDescription(String description) {
        editor.putString(KEY_DESCRIPTION,description);
        // commit changes
        editor.commit();
    }

    public void setKeyPropertyType(String propertyType) {
        editor.putString(KEY_PROPERTY_TYPE,propertyType);
        // commit changes
        editor.commit();
    }

    public void setKeyOfficeType(String officeType) {
        editor.putString(KEY_OFFICE_TYPE,officeType);
        // commit changes
        editor.commit();
    }

    public void setKeyLatitude(String latitude) {
        editor.putString(KEY_LATITUDE,latitude);
        // commit changes
        editor.commit();
    }

    public void setKeyLongitude(String longitude) {
        editor.putString(KEY_LONGITUDE,longitude);
        // commit changes
        editor.commit();
    }

    public void setKeyAddress(String address) {
        editor.putString(KEY_ADDRESS,address);
        // commit changes
        editor.commit();
    }

    public void setKeyCountryName(String countryName) {
        editor.putString(KEY_COUNTRY_NAME,countryName);
        // commit changes
        editor.commit();
    }

    public void setKeyStateName(String stateName) {
        editor.putString(KEY_STATE_NAME,stateName);
        // commit changes
        editor.commit();
    }

    public void setKeyCityName(String cityName) {
        editor.putString(KEY_CITY_NAME,cityName);
        // commit changes
        editor.commit();
    }

    public void setKeyCurrencyName(String currencyName) {
        editor.putString(KEY_CURRENCY_NAME,currencyName);
        // commit changes
        editor.commit();
    }

    public void setKeyRefundType(String refundType) {
        editor.putString(KEY_REFUND_TYPE,refundType);
        // commit changes
        editor.commit();
    }

    public void setKeySpaceAccomodates(String spaceAccomodates) {
        editor.putString(KEY_SPACE_ACCOMODATES,spaceAccomodates);
        // commit changes
        editor.commit();
    }

    public void setKeyPrice(String price) {
        editor.putString(KEY_PRICE,price);
        // commit changes
        editor.commit();
    }

    public void setKeyImage(String image) {
        editor.putString(KEY_IMAGE,image);
        // commit changes
        editor.commit();
    }

    public void setKeyDistance(String distance) {
        editor.putString(KEY_DISTANCE,distance);
        // commit changes
        editor.commit();
    }

    public void setKeyFavoured(boolean favoured) {
        editor.putBoolean(KEY_FAVOURED,favoured);
        // commit changes
        editor.commit();
    }

    public void setKeyRatings(String ratings) {
        editor.putString(KEY_RATINGS,ratings);
        // commit changes
        editor.commit();
    }

    /**
     * Get history
     * */

    public  String getKeyPropertyId(){
        return pref.getString(KEY_PROPERTY_ID,null);
    }

    public  String getKeyPropertyName(){
        return pref.getString(KEY_PROPERTY_NAME,null);
    }

    public  String getKeyDescription(){
        return pref.getString(KEY_DESCRIPTION,null);
    }

    public  String getKeyPropertyType(){
        return pref.getString(KEY_PROPERTY_TYPE,null);
    }

    public  String getKeyOfficeType(){
        return pref.getString(KEY_OFFICE_TYPE,null);
    }

    public  String getKeyLatitude(){
        return pref.getString(KEY_LATITUDE,null);
    }

    public  String getKeyLongitude(){
        return pref.getString(KEY_LONGITUDE,null);
    }

    public  String getKeyCountryName(){
        return pref.getString(KEY_COUNTRY_NAME,null);
    }

    public  String getKeyStateName(){
        return pref.getString(KEY_STATE_NAME,null);
    }

    public  String getKeyCityName(){
        return pref.getString(KEY_CITY_NAME,null);
    }

    public  String getKeyCurrencyName(){
        return pref.getString(KEY_CURRENCY_NAME,null);
    }

    public  String getKeyPrice(){
        return pref.getString(KEY_PRICE,null);
    }

    public  String getKeyImage(){
        return pref.getString(KEY_IMAGE,null);
    }

    public  String getKeyDistance(){
        return pref.getString(KEY_DISTANCE,null);
    }

    public  boolean getKeyFavoured(){
        return pref.getBoolean(KEY_FAVOURED,false);
    }

    public  String getKeyRatings(){
        return pref.getString(KEY_RATINGS,null);
    }

    /**
     *Clear properties details data
     **/

    public void cleardata(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

}
