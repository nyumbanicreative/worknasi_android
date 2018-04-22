package worknasi.model;

import java.io.Serializable;

/**
 * Created by user on 1/2/2018.
 */

public class ReasonsList implements Serializable {

    private String reason;

    public ReasonsList() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
