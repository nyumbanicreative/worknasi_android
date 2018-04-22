package worknasi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/12/2018.
 */

public class ResultItemListSingleTon {

    private static ResultItemListSingleTon mInstance;
    private List<NearmeItem> list = null;

    public static ResultItemListSingleTon getInstance() {
        if(mInstance == null)
            mInstance = new ResultItemListSingleTon();

        return mInstance;
    }

    private ResultItemListSingleTon() {
        list = new ArrayList<NearmeItem>();
    }
    // retrieve array from anywhere
    public List<NearmeItem> getArray() {
        return this.list;
    }
    //Add element to array
    public void addToArray(NearmeItem item) {
        list.add(item);
    }

}
