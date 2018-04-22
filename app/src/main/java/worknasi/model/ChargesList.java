package worknasi.model;

import java.io.Serializable;

/**
 * Created by user on 1/2/2018.
 */

public class ChargesList implements Serializable {

    private String charge;

    public ChargesList() {
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }
}
