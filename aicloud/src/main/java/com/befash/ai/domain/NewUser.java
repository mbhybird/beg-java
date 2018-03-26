package com.befash.ai.domain;

/**
 * Created by NickChung on 01/03/2018.
 */
public class NewUser {

    private Integer UID;
    private String Photo;
    private String Info;

    public Integer getUID() {
        return UID;
    }

    public void setUID(Integer UID) {
        this.UID = UID;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }
}