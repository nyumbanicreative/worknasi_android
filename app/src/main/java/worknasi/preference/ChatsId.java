package worknasi.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2/6/2018.
 */

public class ChatsId {

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
    private static final String PREF_NAME = "chatsid";

    // All Shared Preferences Keys

    //Country information
    public static final String KEY_CHAT_ID = "chat_id";

    public ChatsId(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void addId(String chat_id){
        editor.putString(KEY_CHAT_ID, chat_id);
        // commit changes
        editor.commit();
    }

    /*
    get codes information
     */
    public  String getKeyChatId() {
        return pref.getString(KEY_CHAT_ID,"");
    }

}
