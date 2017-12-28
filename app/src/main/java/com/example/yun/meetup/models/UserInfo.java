package com.example.yun.meetup.models;

import java.io.Serializable;

/**
 * Created by alessio on 02-Dec-17.
 */

public class UserInfo implements Serializable {

    private String _id = "";
    private String email = "";
    private String password = "";
    private String name = "";
    private boolean isAdmin = false;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
