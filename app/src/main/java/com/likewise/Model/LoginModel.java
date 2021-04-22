package com.likewise.Model;

import androidx.annotation.NonNull;

public class LoginModel {
    private String email;
    private String password;

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

    public LoginModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginModel() {
    }

    @NonNull
    @Override
    public String toString() {
        return "email:"+email+"\n"+
                "password:"+password;
    }
}
