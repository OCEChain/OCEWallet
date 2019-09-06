package com.idea.jgw.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by idea on 2018/6/12.
 */

public class UserInfo implements Parcelable {
    /**
     * Uid : 538d5da285a54571
     * Nickname : nike
     * Face : /upload/upload/user/13425133583/24067d1188171594ca2054fa7167f51e.jpg
     * Sex : 0
     * Audit_status : 0
     * Invite_num : 363639
     * Invite_man_num : 0
     * Invite_url : /invite?num=363639
     */

    private String Uid;
    private String Nickname;
    private String Face;
    private int Sex;
    private int Audit_status;
    private String Invite_num;
    private int Invite_man_num;
    private String Invite_url;

    public String getUid() {
        return Uid;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }

    public String getFace() {
        return Face;
    }

    public void setFace(String Face) {
        this.Face = Face;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int Sex) {
        this.Sex = Sex;
    }

    public int getAudit_status() {
        return Audit_status;
    }

    public void setAudit_status(int Audit_status) {
        this.Audit_status = Audit_status;
    }

    public String getInvite_num() {
        return Invite_num;
    }

    public void setInvite_num(String Invite_num) {
        this.Invite_num = Invite_num;
    }

    public int getInvite_man_num() {
        return Invite_man_num;
    }

    public void setInvite_man_num(int Invite_man_num) {
        this.Invite_man_num = Invite_man_num;
    }

    public String getInvite_url() {
        return Invite_url;
    }

    public void setInvite_url(String Invite_url) {
        this.Invite_url = Invite_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Uid);
        dest.writeString(this.Nickname);
        dest.writeString(this.Face);
        dest.writeInt(this.Sex);
        dest.writeInt(this.Audit_status);
        dest.writeString(this.Invite_num);
        dest.writeInt(this.Invite_man_num);
        dest.writeString(this.Invite_url);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.Uid = in.readString();
        this.Nickname = in.readString();
        this.Face = in.readString();
        this.Sex = in.readInt();
        this.Audit_status = in.readInt();
        this.Invite_num = in.readString();
        this.Invite_man_num = in.readInt();
        this.Invite_url = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
