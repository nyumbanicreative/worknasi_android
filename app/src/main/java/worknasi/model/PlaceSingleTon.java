package worknasi.model;

/**
 * Created by user on 1/9/2018.
 */

public class PlaceSingleTon {
    String[] placesId = new String[1000];
    private static PlaceSingleTon myObj;
    /**
     * Create private constructor
     */
    private PlaceSingleTon(){

    }
    /**
     * Create a static method to get instance.
     */
    public static PlaceSingleTon getInstance(){
        if(myObj == null){
            myObj = new PlaceSingleTon();
        }
        return myObj;
    }

    public String getPlacesId(int index) {
        return placesId[index];
    }

    public void setPlacesId(String Id, int index) {
        this.placesId[index] = Id;
    }

    /*public void getSomeThing(){
        // do something here
        System.out.println("I am here....");
    }

    public static void main(String a[]){
        PlaceSingleTon st = PlaceSingleTon.getInstance();
        st.getSomeThing();
    }*/

}
