package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 2/1/2018.
 */

public class CreatePlan {

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
    private static final String PREF_NAME = "createplan";

    // Sharedpref key
    public static final String KEY_PEOPLE_PER_DURATION = "people_per_duration";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DURATION_TYPE = "duration_type";
    public static final String KEY_SPACE_COUNT = "space_count";
    public static final String KEY_MIN_PEOPLE = "min_people";
    public static final String KEY_PROPERTY_NAME = "property_name";
    public static final String KEY_PLAN_TYPE_NAME = "plan_type_name";
    public static final String KEY_CURRENCY_NAME = "currency_name";
    public static final String KEY_PLAN_DESCRIPTION = "plan_description";

    public CreatePlan(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //save plan
    public void setKeyPeoplePerDuration(String peoplePerDuration) {
        editor.putString(KEY_PEOPLE_PER_DURATION, peoplePerDuration);
        // commit changes
        editor.commit();
    }

    public void setKeyAmount(String amount) {
        editor.putString(KEY_AMOUNT, amount);
        // commit changes
        editor.commit();
    }

    public void setKeyDurationType(String durationType) {
        editor.putString(KEY_DURATION_TYPE, durationType);
        // commit changes
        editor.commit();
    }

    public void setKeySpaceCount(String spaceCount) {
        editor.putString(KEY_SPACE_COUNT, spaceCount);
        // commit changes
        editor.commit();
    }

    public void setKeyMinPeople(String minPeople) {
        editor.putString(KEY_MIN_PEOPLE, minPeople);
        // commit changes
        editor.commit();
    }

    public void setKeyPropertyName(String propertyName) {
        editor.putString(KEY_PROPERTY_NAME, propertyName);
        // commit changes
        editor.commit();
    }

    public void setKeyPlanTypeName(String planTypeName) {
        editor.putString(KEY_PLAN_TYPE_NAME, planTypeName);
        // commit changes
        editor.commit();
    }

    public void setKeyCurrencyName(String currencyName) {
        editor.putString(KEY_CURRENCY_NAME, currencyName);
        // commit changes
        editor.commit();
    }

    public void setKeyPlanDescription(String planDescription) {
        editor.putString(KEY_PLAN_DESCRIPTION, planDescription);
        // commit changes
        editor.commit();
    }


    //Get Plan
    public  String getKeyPeoplePerDuration(){
        return pref.getString(KEY_PEOPLE_PER_DURATION,"");
    }

    public  String getKeyAmount(){
        return pref.getString(KEY_AMOUNT,"");
    }

    public  String getKeyDurationType(){
        return pref.getString(KEY_DURATION_TYPE,"");
    }

    public  String getKeySpaceCount(){
        return pref.getString(KEY_SPACE_COUNT,"");
    }

    public  String getKeyMinPeople(){
        return pref.getString(KEY_MIN_PEOPLE,"");
    }

    public  String getKeyPropertyName(){
        return pref.getString(KEY_MIN_PEOPLE,"");
    }

    public  String getKeyPlanTypeName(){
        return pref.getString(KEY_PLAN_TYPE_NAME,"");
    }

    public  String getKeyCurrencyName(){
        return pref.getString(KEY_CURRENCY_NAME,"");
    }

    public  String getKeyPlanDescription(){
        return pref.getString(KEY_PLAN_DESCRIPTION,"");
    }

}
