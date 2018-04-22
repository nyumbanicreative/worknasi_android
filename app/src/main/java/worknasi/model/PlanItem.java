package worknasi.model;

/**
 * Created by user on 1/25/2018.
 */

public class PlanItem {
    private int plan_id;
    private String plan_description;

    public PlanItem() {
    }

    public int getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(int plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_description() {
        return plan_description;
    }

    public void setPlan_description(String plan_description) {
        this.plan_description = plan_description;
    }
}
