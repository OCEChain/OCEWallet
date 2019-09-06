package com.idea.jgw.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by idea on 2018/6/23.
 */

public class CaculateRecord implements Parcelable {
    /**
     * Id : 1
     * Content : 完善用户信息增加算力
     * Typeid : 1
     * Num : 10
     * Time : 1529726105
     */

    private int Id;
    private String Content;
    private int Typeid;
    private int Num;
    private long Time;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public int getTypeid() {
        return Typeid;
    }

    public void setTypeid(int Typeid) {
        this.Typeid = Typeid;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int Num) {
        this.Num = Num;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long Time) {
        this.Time = Time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.Id);
        dest.writeString(this.Content);
        dest.writeInt(this.Typeid);
        dest.writeInt(this.Num);
        dest.writeLong(this.Time);
    }

    public CaculateRecord() {
    }

    protected CaculateRecord(Parcel in) {
        this.Id = in.readInt();
        this.Content = in.readString();
        this.Typeid = in.readInt();
        this.Num = in.readInt();
        this.Time = in.readInt();
    }

    public static final Parcelable.Creator<CaculateRecord> CREATOR = new Parcelable.Creator<CaculateRecord>() {
        @Override
        public CaculateRecord createFromParcel(Parcel source) {
            return new CaculateRecord(source);
        }

        @Override
        public CaculateRecord[] newArray(int size) {
            return new CaculateRecord[size];
        }
    };
}
