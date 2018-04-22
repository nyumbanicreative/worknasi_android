package worknasi.model;

/**
 * Created by user on 1/19/2018.
 */

public class ItemMyfavorite {

    private String property_id;
    private String text_property_name;
    private String text_distance;
    private String text_address;
    private String ratingBar;
    private String text_price;
    private String text_currency;
    private String feedimage;
    private int position;

    public ItemMyfavorite() {
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public String getText_property_name() {
        return text_property_name;
    }

    public void setText_property_name(String text_property_name) {
        this.text_property_name = text_property_name;
    }

    public String getText_distance() {
        return text_distance;
    }

    public void setText_distance(String text_distance) {
        this.text_distance = text_distance;
    }

    public String getText_address() {
        return text_address;
    }

    public void setText_address(String text_address) {
        this.text_address = text_address;
    }

    public String getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(String ratingBar) {
        this.ratingBar = ratingBar;
    }

    public String getText_price() {
        return text_price;
    }

    public void setText_price(String text_price) {
        this.text_price = text_price;
    }

    public String getText_currency() {
        return text_currency;
    }

    public void setText_currency(String text_currency) {
        this.text_currency = text_currency;
    }

    public String getFeedimage() {
        return feedimage;
    }

    public void setFeedimage(String feedimage) {
        this.feedimage = feedimage;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
