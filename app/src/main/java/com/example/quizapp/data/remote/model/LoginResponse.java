package com.example.quizapp.data.remote.model;

public class LoginResponse {
    public String access_token;
    public String refresh_token;
    public int expires_in;
    public UserInfo user;
}
