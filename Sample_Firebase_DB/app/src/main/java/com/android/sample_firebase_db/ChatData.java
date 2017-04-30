package com.android.sample_firebase_db;

public class ChatData {

    private String userName;
    private String msg;

    public ChatData() {

    }

    public ChatData(String userName, String msg) {

        this.userName = userName;
        this.msg = msg;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }
}
