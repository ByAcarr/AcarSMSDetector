package com.sehmusacar.AcarSMSDetector.Utils;

public class MsgData {

    String msgTitle,msgBody,msgDate,id,serviceNum;
            long msgDateLong;
    Boolean isChecked, isSpam;


    public void setId(String id) {
        this.id = id;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public void setChecked(Boolean checked) {
        this.isChecked = checked;
    }

    public void setSpam(Boolean spam) {
        this.isSpam = spam;
    }

    public void setServiceNum(String serviceNum) {
        this.serviceNum = serviceNum;
    }

    public void setMsgDateLong(long msgDateLong) {
        this.msgDateLong = msgDateLong;
    }

    public String getId() {
        return id;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public Boolean getSpam() {
        return isSpam;
    }

    public long getMsgDateLong() {
        return msgDateLong;
    }

    public String getServiceNum() {
        return serviceNum;
    }

}
