package worknasi.model;

import java.util.LinkedList;

/**
 * Created by user on 2/6/2018.
 */

public class FavoriteBottomSheet {

    LinkedList<FavoriteBottomItem> sheets = new LinkedList<>();
    //create singleton
    private static FavoriteBottomSheet myObj;
    /**
     * Create private constructor
     */
    private FavoriteBottomSheet(){

    }
    /**
     * Create a static method to get instance.
     */
    public static FavoriteBottomSheet getInstance(){
        if(myObj == null){
            myObj = new FavoriteBottomSheet();
        }
        return myObj;
    }

    public void addState(String user_id,String property_id,boolean favorite){
        FavoriteBottomItem item = FavoriteBottomItem.getInstance();
        item.setState(user_id,property_id,favorite);
        sheets.add(item);
    }

    public void deleteState(String user_id,String property_id){
        if(isStateAvailable(user_id,property_id)){
            for(int i=0; i<sheets.size(); i++){
                FavoriteBottomItem item = sheets.get(i);
                if(user_id.equals(item.getUser_id()) && property_id.equals(item.getProperty_id())){
                    //delete
                    sheets.remove(i);
                    break;
                }else {
                    break;
                }
            }
        }
    }

    public boolean getFavoriteState(String user_id,String property_id){
        boolean state=false;
        for(int i=0; i<sheets.size(); i++){
            FavoriteBottomItem item = sheets.get(i);
            if(user_id.equals(item.getUser_id()) && property_id.equals(item.getProperty_id())){
                state = item.isFavorite();
                break;
            }else {
                state = false;
                break;
            }
        }
        return state;
    }

    public  boolean isStateAvailable(String user_id,String property_id){
        boolean getstate=false;
        for(int i=0; i<sheets.size(); i++){
            FavoriteBottomItem item = sheets.get(i);
            if(user_id.equals(item.getUser_id()) && property_id.equals(item.getProperty_id())){
                getstate = true;
                break;
            }else {
                getstate = false;
                break;
            }
        }
        return getstate;
    }

    public boolean isEmpty(){
        return sheets.size()==0? true:false;
    }

}
