package com.siecom.framework.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * 保存是否信息的Bean
 * 
 * @author gang
 * 
 */
public class IdentityInfoBean implements Parcelable {
    /**
     * 姓名
     */
    public String fullName;
    /**
     * 性别 1 男 0 女
     */
    public String sex;
    /**
     * 证件照片
     */
    public Bitmap icon;
    /**
     * 民族
     */
    public String nation;

    /**
     * 生日
     */
    public String birthday;

    /**
     * 证件上的地址
     */
    public String idAddr;

    /**
     * 证件号码
     */
    public String idNo;

    /**
     * 发证机关
     */
    public String idOrg;

    /**
     * 证件有效期 开始 时间格式 yyyyMMdd
     */
    public String beginDate;

    /**
     * 证件有效期 结束
     */
    public String endDate;
    
    public byte[] fingerByte;
    

    @Override
    public int describeContents() {
        return 0;
    }

    
    public IdentityInfoBean() {
    }
    
    public IdentityInfoBean(Parcel source) {
        source.readString();
        source.readInt();
        source.readParcelable(getClass().getClassLoader());
        source.readString();
        source.readString();
        source.readString();
        source.readString();
        source.readString();
        source.readString();
        source.readString();
        source.readByteArray(fingerByte);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(sex);
        dest.writeParcelable(icon, flags);
        dest.writeString(nation);
        dest.writeString(birthday);
        dest.writeString(idAddr);
        dest.writeString(idNo);
        dest.writeString(idOrg);
        dest.writeString(beginDate);
        dest.writeString(endDate);
        dest.writeByteArray(fingerByte);
    }

    public static final Creator<IdentityInfoBean> CREATOR = new Creator<IdentityInfoBean>() {
        public IdentityInfoBean createFromParcel(Parcel source) {
            return new IdentityInfoBean(source);
        }

        public IdentityInfoBean[] newArray(int size) {
            return new IdentityInfoBean[size];
        }
    };
}
