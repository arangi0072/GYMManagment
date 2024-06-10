package com.arpit.gymmanagment.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Plans {
    private String plan_details;
    private String time;
    private String fee;

    public Plans() {
    }

    public Plans(String plan_details, String time, String fee) {
        this.plan_details = plan_details;
        this.time = time;
        this.fee = fee;
    }

    public String getPlan_details() {
        return plan_details;
    }

    public void setPlan_details(String plan_details) {
        this.plan_details = plan_details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("plan_details", plan_details);
        result.put("time", time);
        result.put("fee", fee);
        return result;
    }
}
