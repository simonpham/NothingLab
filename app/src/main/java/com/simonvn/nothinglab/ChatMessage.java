package com.simonvn.nothinglab;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String messageUID;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser, String messageUID) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageUID = messageUID;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageUID() {
        return messageUID;
    }

    public void setMessageUID(String messageUID) {
        this.messageUID = messageUID;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }


}


