package worknasi.model;

/**
 * Created by user on 1/10/2018.
 */

public class CurrentLocationSingleTon {
    String latitude;
    String longitude;
    private static CurrentLocationSingleTon myObj;
    /**
     * Create private constructor
     */
    private CurrentLocationSingleTon(){

    }
    /**
     * Create a static method to get instance.
     */
    public static CurrentLocationSingleTon getInstance(){
        if(myObj == null){
            myObj = new CurrentLocationSingleTon();
        }
        return myObj;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitudeLongitude(String latitude,String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
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
