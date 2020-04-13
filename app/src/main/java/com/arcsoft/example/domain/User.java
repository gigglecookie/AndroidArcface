package com.arcsoft.example.domain;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -8064351370687888128L;
    private String userName;
    private String userId;
    private String userSex;
    private String userFeature;
    private String userImage;

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", userSex='" + userSex + '\'' +
                ", userFeature='" + userFeature + '\'' +
                ", userImage='" + userImage + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserFeature() {
        return userFeature;
    }

    public void setUserFeature(String userFeature) {
        this.userFeature = userFeature;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public User(String userName, String userId, String userSex, String userFeature, String userImage) {
        this.userName = userName;
        this.userId = userId;
        this.userSex = userSex;
        this.userFeature = userFeature;
        this.userImage = userImage;
    }

    public User() {
    }

}
