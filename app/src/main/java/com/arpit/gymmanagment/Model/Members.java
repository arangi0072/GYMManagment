package com.arpit.gymmanagment.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Members {
    private String  ID;
    private String NAME;
    private String PHONE;
    private String ADDRESS;
    private boolean IMAGE;
    private String EXPIRY_DATE;
    private String PLAN_DETAILS;
    private String PLAN_FEE;

    private boolean image_updated;
    private String DUE_AMOUNT;

    public Members() {
    }
    public Members(String ID ,  String NAME, String PHONE, String ADDRESS, String EXPIRY_DATE, String PLAN_DETAILS, String DUE_AMOUNT, String PLAN_FEE, boolean IMAGE) {
        this.ID = ID;
        this.PLAN_FEE = PLAN_FEE;
        this.NAME = NAME;
        this.PHONE = PHONE;
        this.IMAGE = IMAGE;
        this.ADDRESS = ADDRESS;
        this.EXPIRY_DATE = EXPIRY_DATE;
        this.PLAN_DETAILS = PLAN_DETAILS;
        this.DUE_AMOUNT = DUE_AMOUNT;
    }
    public Members(String NAME, String PHONE, String ADDRESS, String EXPIRY_DATE, String PLAN_DETAILS, String DUE_AMOUNT, String PLAN_FEE, boolean IMAGE) {
        this.PLAN_FEE = PLAN_FEE;
        this.NAME = NAME;
        this.PHONE = PHONE;
        this.IMAGE = IMAGE;
        this.ADDRESS = ADDRESS;
        this.EXPIRY_DATE = EXPIRY_DATE;
        this.PLAN_DETAILS = PLAN_DETAILS;
        this.DUE_AMOUNT = DUE_AMOUNT;
    }

    public String  getID() {
        return ID;
    }

    public void setID(String  ID) {
        this.ID = ID;
    }

    public boolean isIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(boolean IMAGE) {
        this.IMAGE = IMAGE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }


    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getEXPIRY_DATE() {
        return EXPIRY_DATE;
    }

    public void setEXPIRY_DATE(String EXPIRY_DATE) {
        this.EXPIRY_DATE = EXPIRY_DATE;
    }

    public void setPLAN_DETAILS(String PLAN_DETAILS) {
        this.PLAN_DETAILS = PLAN_DETAILS;
    }

    public String getPLAN_DETAILS() {
        return PLAN_DETAILS;
    }

    public boolean isImage_updated() {
        return image_updated;
    }

    public void setImage_updated(boolean image_updated) {
        this.image_updated = image_updated;
    }

    public String getDUE_AMOUNT() {
        return DUE_AMOUNT;
    }

    public void setDUE_AMOUNT(String DUE_AMOUNT) {
        this.DUE_AMOUNT = DUE_AMOUNT;
    }

    public String getPLAN_FEE() {
        return PLAN_FEE;
    }

    public void setPLAN_FEE(String PLAN_FEE) {
        this.PLAN_FEE = PLAN_FEE;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", NAME);
        result.put("Phone", PHONE );
        result.put("address", ADDRESS);
        result.put("image", IMAGE);
        result.put("expiry", EXPIRY_DATE);
        result.put("plan_details", PLAN_DETAILS);
        result.put("plan_fee", PLAN_FEE);
        result.put("image_updated" , false);
        result.put("due_payment", DUE_AMOUNT);
        return result;
    }
}
