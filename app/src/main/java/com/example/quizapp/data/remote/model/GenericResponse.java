package com.example.quizapp.data.remote.model;

public class GenericResponse<T> {
    public String status;
    public T data;
    public String message;
}
