package com.example.quizapp.data.remote.model;

public class ResetPasswordRequest {
    public String email;
    public String otp;
    public String new_password;

    public ResetPasswordRequest(String email, String otp, String new_password) {
        this.email = email;
        this.otp = otp;
        this.new_password = new_password;
    }
}
