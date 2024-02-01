package com.sehmusacar.AcarSMSDetector.Utils;

public class SearchData {

    String messageID,message;
    int type;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public String getMessageID() {
        return messageID;
    }

}
