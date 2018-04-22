package worknasi.model;

import java.io.Serializable;

/**
 * Created by user on 1/2/2018.
 */

public class AmenitiesList implements Serializable {
    private String Amenity;

    public AmenitiesList() {
    }

    public AmenitiesList(String amenity) {
        Amenity = amenity;
    }

    public String getAmenity() {
        return Amenity;
    }

    public void setAmenity(String amenity) {
        Amenity = amenity;
    }
}
