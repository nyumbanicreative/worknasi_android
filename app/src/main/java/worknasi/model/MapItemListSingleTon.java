package worknasi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/12/2018.
 */

public class MapItemListSingleTon {

    private static MapItemListSingleTon mInstance;
    private List<MapItem> list = null;

    public static MapItemListSingleTon getInstance() {
        if(mInstance == null)
            mInstance = new MapItemListSingleTon();

        return mInstance;
    }

    private MapItemListSingleTon() {
        list = new ArrayList<MapItem>();
    }
    // retrieve array from anywhere
    public List<MapItem> getArray() {
        return this.list;
    }
    //Add element to array
    public void addToArray(MapItem item) {
        list.add(item);
    }

}
