package com.jackong.jobbooks;

public class UserDatabaseName {
    private String email;

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String convertEmailToDbName (String email){
        String a[], name = "";

        a = email.split("@|\\.");
        for(int i = 0; i < a.length ; i++){
            if (i != a.length - 1){
                name = name + a[i] + "_";
            }
            else {
                name = name + a[i];
            }
        }
        return name;
    }
}
