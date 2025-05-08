package com.example.chymchatapp;

public class msgModelClass {
    String msg;
    String senderId;
    long timestamp;
    boolean isAudio;//for audio messages
    int audId;

    public msgModelClass() {
    }

    //constructor for textMessages
    public msgModelClass(String msg, String senderId, long timestamp) {
        this.msg = msg;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    //constructor for audioMessages
    public msgModelClass(int audId,String senderId){
        this.audId=audId;
        this.senderId=senderId;
        this.isAudio=true;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public void setAudio(boolean audio) {
        isAudio = audio;
    }

    public int getAudId() {
        return audId;
    }

    public void setAudId(int audId) {
        this.audId = audId;
    }
}
