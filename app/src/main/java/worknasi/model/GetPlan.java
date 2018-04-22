package worknasi.model;

/**
 * Created by user on 1/26/2018.
 */

public class GetPlan {

    private String property_id;
    private String property_name;
    private String plan_id;
    private String plan_description;
    private String number_of_people;
    private String start_date;
    private String start_time;
    private String duration;

    public GetPlan() {
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public String getProperty_name() {
        return property_name == null? "":property_name;
    }

    public void setProperty_name(String property_name) {
        this.property_name = property_name;
    }

    public String getPlan_id() {
        return plan_id == null? "":plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_description() {
        return plan_description == null? "":plan_description;
    }

    public void setPlan_description(String plan_description) {
        this.plan_description = plan_description;
    }

    public String getNumber_of_people() {
        return number_of_people == null? "":number_of_people;
    }

    public void setNumber_of_people(String number_of_people) {
        this.number_of_people = number_of_people;
    }

    public String getStart_date() {
        return start_date == null? "":start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStart_time() {
        return start_time == null? "":start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDuration() {
        return duration == null? "":duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
