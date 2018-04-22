package worknasi.config;

/**
 * Created by user on 12/12/2017.
 */

public class AppConfig {

    //links

    //private  static String base = "http://192.168.43.159:8888";

    private  static String base = "http://165.227.7.53/worknasiapi/index.php";
    //public   static String LINK_MAP = base+ "/worknasi/";
    public   static String LINK_MAP = base+ "/properties/nearmespaces";
    public   static String LINK_FILTER_FEED = base+ "/worknasijson/";

    public   static String LINK_FETCH_COUNTRY = "http://petronite.nyumbanicreative.com/worknasijson/countries.php";
    public   static String LINK_FETCH_STATE = "http://petronite.nyumbanicreative.com/worknasijson/states.php";
    public   static String LINK_FETCH_CITY = "http://petronite.nyumbanicreative.com/worknasijson/cities.php";

    public   static String LINK_FETCH_AVAILABLESPACE = "http://petronite.nyumbanicreative.com/worknasijson/available-spaces.php";
    public   static String LINK_FETCH_FILTEREDSPACE = "http://petronite.nyumbanicreative.com/worknasijson/filtered.php";
    public   static String LINK_FETCH_PREVIEW = base+"/properties/previewspace";
    public   static String LINK_LOGIN = base+"/user/login";
    public   static String LINK_REGISTER = base+"/user/login";
    public   static String LINK_REVIEW = base+"/properties/reviews";
    public   static String LINK_SENDREVIEW = base+"/properties/addreview";
    public   static String LINK_NEARME = base+ "/properties/nearmespaces";
    public   static String LINK_MYFAVORITE = base+ "/properties/favouritespaces";
    public   static String LINK_RESULT_SEARCH_LIST = base+ "/properties/searchresults";
    public   static String LINK_INBOX = base+ "/messages/conversations";
    public   static String LINK_DELETEINBOX = base+ "/properties/deletechats";
    public   static String LINK_CHAT = base+ "/messages/chat";
    public   static String LINK_CHAT_SEND = base+ "/messages/sendmsg";

    public   static String LINK_FAVORITE = base+ "/properties/favourite";

    public   static String QUERY_KEY_AUTOCOMPLETE_PLACES = "AIzaSyCbLHvL1Mb54T6yvYwnPAOjtaHkiI1fRX0";
    public   static String LINK_KEY_AUTOCOMPLETE_PLACES = "https://maps.googleapis.com/maps/api/place/details/json";

    public   static String LINK_PLANS = base+"/properties/propertyplans";
    public   static String LINK_RENT = base+"/properties/rentspace";
    public   static String LINK_SUBMITRENT = base+"/properties/submitrentform";


    public   static String LINK_RESERVEDSPACE = base+"/properties/reservedspaces";
    public   static String LINK_BOOKEDSPACE = base+"/properties/bookedspaces";


    // broadcast receiver intent filters
    public static final String INBOX_BROADCAST = "inboxbroadcast";
    public static final String CHAT_BROADCAST = "chatbroadcast";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    //offset
    public static final int OFFSETSIZE = 20;

}

