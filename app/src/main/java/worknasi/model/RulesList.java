package worknasi.model;

import java.io.Serializable;

/**
 * Created by user on 1/2/2018.
 */

public class RulesList implements Serializable {

    private String rule;

    public RulesList() {
    }

    public RulesList(String rule) {
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

}
