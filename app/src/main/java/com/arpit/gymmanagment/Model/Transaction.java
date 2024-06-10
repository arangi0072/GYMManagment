package com.arpit.gymmanagment.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private String PAY_DATE;
    private String AMOUNT;
    private String PAYMENT_METHOD;
    private String FOR_REASON;

    public Transaction(String PAY_DATE, String AMOUNT, String PAYMENT_METHOD, String FOR_REASON) {
        this.PAY_DATE = PAY_DATE;
        this.AMOUNT = AMOUNT;
        this.PAYMENT_METHOD = PAYMENT_METHOD;
        this.FOR_REASON = FOR_REASON;
    }
    public Transaction(String AMOUNT, String PAYMENT_METHOD, String FOR_REASON) {
        this.PAY_DATE = PAY_DATE;
        this.AMOUNT = AMOUNT;
        this.PAYMENT_METHOD = PAYMENT_METHOD;
        this.FOR_REASON = FOR_REASON;
    }
    public Transaction() {
    }

    public String getPAY_DATE() {
        return PAY_DATE;
    }

    public void setPAY_DATE(String PAY_DATE) {
        this.PAY_DATE = PAY_DATE;
    }

    public String getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getPAYMENT_METHOD() {
        return PAYMENT_METHOD;
    }

    public void setPAYMENT_METHOD(String PAYMENT_METHOD) {
        this.PAYMENT_METHOD = PAYMENT_METHOD;
    }

    public String getFOR_REASON() {
        return FOR_REASON;
    }

    public void setFOR_REASON(String FOR_REASON) {
        this.FOR_REASON = FOR_REASON;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("amount" , AMOUNT);
        result.put("payment_method" , PAYMENT_METHOD);
        result.put("reason" , FOR_REASON);
        return result;
    }
}
