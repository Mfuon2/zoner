package com.kuzasystems.zoner;

import java.sql.Date;

/**
 * Created by victor on 23-Oct-18.
 */

public class MyMessage {
    private int messsageId;
    private int senderId;
    private int recipientId;
    private String recipientName;
    private String message;
    private String sentOn;
    private int status;

    public MyMessage(int messsageId, int senderId, int recipientId, String recipientName, String message, String sentOn, int status) {
        this.messsageId = messsageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.message = message;
        this.sentOn = sentOn;
        this.status = status;
    }

    public int getMesssageId() {
        return messsageId;
    }

    public void setMesssageId(int messsageId) {
        this.messsageId = messsageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentOn() {
        return sentOn;
    }

    public void setSentOn(String sentOn) {
        this.sentOn = sentOn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
