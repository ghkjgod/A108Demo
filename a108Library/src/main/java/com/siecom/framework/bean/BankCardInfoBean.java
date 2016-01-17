package com.siecom.framework.bean;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * 银行卡信息 Bean
 * 
 * @author gang
 * 
 */
public class BankCardInfoBean implements Parcelable {
    /**
     * 卡号
     */
    public String cardNo;

    /**
     * 一 磁道信息
     */
    public String oneMagneticTrack;

    /**
     * 二 磁道信息
     */
    public String twoMagneticTrack;

    /**
     * 三磁道信息
     */
    public String threeMagneticTrack;

    /**
     * IC卡芯片数据
     */
    public String ICChipData;

    /**
     * 卡片类型 0 IC卡 1 磁条卡 2 NFC
     */

    public int cardType;

    @Override
    public int describeContents() {
        return 0;
    }

    public BankCardInfoBean() {

    }

    public BankCardInfoBean(Parcel source) {
        cardNo = source.readString();
        oneMagneticTrack = source.readString();
        twoMagneticTrack = source.readString();
        threeMagneticTrack = source.readString();
        ICChipData = source.readString();
        cardType = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardNo);
        dest.writeString(oneMagneticTrack);
        dest.writeString(twoMagneticTrack);
        dest.writeString(threeMagneticTrack);
        dest.writeString(ICChipData);
        dest.writeInt(cardType);
    }

    public static final Creator<BankCardInfoBean> CREATOR = new Creator<BankCardInfoBean>() {
        public BankCardInfoBean createFromParcel(Parcel source) {
            return new BankCardInfoBean(source);
        }

        public BankCardInfoBean[] newArray(int size) {
            return new BankCardInfoBean[size];
        }
    };

    @Override
    public String toString() {
        return "BankCardInfoBean [cardNo=" + cardNo + ", oneMagneticTrack="
                + oneMagneticTrack + ", twoMagneticTrack=" + twoMagneticTrack
                + ", threeMagneticTrack=" + threeMagneticTrack
                + ", ICChipData=" + ICChipData + ", cardType=" + cardType + "]";
    }
}
