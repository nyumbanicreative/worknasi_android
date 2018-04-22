package worknasi.model;

/**
 * Created by user on 1/4/2018.
 */

public class GetBooking {

    String _rentingDate;
    String _numberOfPeople;

    public GetBooking() {
    }

    public String get_rentingDate() {
        return _rentingDate;
    }

    public void set_rentingDate(String _rentingDate) {
        this._rentingDate = _rentingDate;
    }

    public String get_numberOfPeople() {
        return _numberOfPeople;
    }

    public void set_numberOfPeople(String _numberOfPeople) {
        this._numberOfPeople = _numberOfPeople;
    }

}
