package com.example.quizapp.data.remote.model;

public class AnswerRecord {
    public int question_id;
    public String selected_option;
    public boolean is_correct;
    public int time_spent_secs;

    public AnswerRecord(int question_id, String selected_option, boolean is_correct, int time_spent_secs) {
        this.question_id = question_id;
        this.selected_option = selected_option;
        this.is_correct = is_correct;
        this.time_spent_secs = time_spent_secs;
    }
}
