package com.siecom.framework.bean;

/**
 * Created by Administrator on 2015/12/13.
 */
public class TradeLogBean {
    public String amount;
    public String otherAmount;
    public String date;
    public String time;
    public String CountryCode;
    public String TransCurrCode;
    public String MerchName;
    public int TransNo;
    public String TransType;

    public void setTransNo(int transNo) {
        TransNo = transNo;
    }

    public void setTransType(String transType) {
        TransType = transType;
    }

    public int getTransNo() {
        return TransNo;
    }

    public String getTransType() {
        return TransType;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setOtherAmount(String otherAmount) {
        this.otherAmount = otherAmount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public void setTransCurrCode(String transCurrCode) {
        TransCurrCode = transCurrCode;
    }

    public void setMerchName(String merchName) {
        MerchName = merchName;
    }

    public String getMerchName() {
        return MerchName;
    }

    public String getAmount() {
        return amount;
    }

    public String getOtherAmount() {
        return otherAmount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public String getTransCurrCode() {
        return TransCurrCode;
    }
}
