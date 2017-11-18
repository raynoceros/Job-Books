package com.jackong.jobbooks;

public class ChatListModel {
    String user_name;
    String last_msg;
    String last_msg_time;
    String image_link;
    String user_id;

    public ChatListModel(String userid, String username, String lastmsg, String lastmsgtime, String imagelink){
        this.user_id = userid;
        this.user_name = username;
        this.last_msg = lastmsg;
        this.last_msg_time = lastmsgtime;
        this.image_link = imagelink;
    }
    public String getUser_id() { return user_id; }

    public String getUser_name(){
        return user_name;
    }

    public String getLast_msg(){
        return last_msg;
    }

    public  String getLast_msg_time(){
        return last_msg_time;
    }

    public String getImage_link(){
        return image_link;
    }
}
