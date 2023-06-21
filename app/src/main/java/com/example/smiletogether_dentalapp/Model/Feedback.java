package com.example.smiletogether_dentalapp.Model;

public class Feedback {
    private int grade;


    public Feedback() {
    }

    public Feedback(int grade) {
        this.grade = grade;

    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "nota=" + grade +
                '}';
    }
}