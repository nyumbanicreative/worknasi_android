package worknasi.model;

/**
 * Created by user on 2/6/2018.
 */

public class FavoriteBottomItem {

    //data
    private String user_id;
    private String property_id;
    private boolean favorite;

    //create singleton
    private static FavoriteBottomItem myObj;
    /**
     * Create private constructor
     */
    private FavoriteBottomItem(){}
    /**
     * Create a static method to get instance.
     */
    public static FavoriteBottomItem getInstance(){
        if(myObj == null){
            myObj = new FavoriteBottomItem();
        }
        return myObj;
    }

    public void setState(String user_id,String property_id,boolean favorite){
        this.user_id = user_id;
        this.property_id = property_id;
        this.favorite = favorite;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getProperty_id() {
        return property_id;
    }

    public boolean isFavorite() {
        return favorite;
    }


}
