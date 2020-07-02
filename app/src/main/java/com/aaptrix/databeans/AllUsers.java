package com.aaptrix.databeans;

/**
 * Created by Amit Baranwal on 3/10/2018.
 */

public class AllUsers
{
    public String user_name;
    public String user_image;
    public String user_status;


    public AllUsers()
    {

    }

    public AllUsers(String user_name, String user_image, String user_status) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.user_status = user_status;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return this.user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_status() {
        return this.user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
}
