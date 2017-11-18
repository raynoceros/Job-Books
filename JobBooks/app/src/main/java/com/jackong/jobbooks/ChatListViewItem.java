package com.jackong.jobbooks;

public class ChatListViewItem {
    private String chat_message;
    private String chat_time;
    private int message_type;

    public ChatListViewItem( String chattime, String chatmsg, int msgtype) {
        this.chat_message = chatmsg;
        this.chat_time = chattime;
        this.message_type = msgtype;
    }

    public String getChat_message() {
        return chat_message;
    }

    public void setChat_message(String chatmsg) {
        this.chat_message = chatmsg;
    }

    public String getChat_time() {
        return chat_time;
    }

    public void setChat_time(String chattime) {
        this.chat_time = chattime;
    }

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int msgtype) {
        this.message_type = msgtype;
    }
}
