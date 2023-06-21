package com.example.smiletogether_dentalapp.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Doctor extends User implements Serializable {
    private String professionalDegree="";
    private String idSpeciality="";
    private List<WorkDay> schedule=new ArrayList<>();
    private String urlProfilePhoto;
    private List<Integer> gradesFeedback;
    private double gradeFeedback;
    private boolean deletedAccount;


    public Doctor() {
    }

    public Doctor(String id, String lastname, String firstname, String email, String nrPhone, String password, String role, String sex, String dateOfBirth, String idSpeciality, String professionalDegree, List<WorkDay> schedule,String urlProfilePhoto,List<Integer> gradesFeedback, double gradeFeedback) {
        super(id, lastname, firstname, email, nrPhone, password,role, sex, dateOfBirth);
        this.professionalDegree = professionalDegree;
        this.idSpeciality = idSpeciality;
        this.schedule = schedule;
        this.urlProfilePhoto=urlProfilePhoto;
        this.gradesFeedback = gradesFeedback;
        this.gradeFeedback = gradeFeedback;
        this.deletedAccount=false;
    }

    public boolean isDeletedAccount() {
        return deletedAccount;
    }

    public void setDeletedAccount(boolean deletedAccount) {
        this.deletedAccount = deletedAccount;
    }

    public List<Integer> getGradesFeedback() {
        return gradesFeedback;
    }

    public void setGradesFeedback(List<Integer> gradesFeedback) {
        this.gradesFeedback = gradesFeedback;
    }

    public double getGradeFeedback() {
        return gradeFeedback;
    }

    public void setGradeFeedback(double gradeFeedback) {
        this.gradeFeedback = gradeFeedback;
    }

    public String getUrlProfilePhoto() {
        return urlProfilePhoto;
    }

    public void setUrlProfilePhoto(String urlProfilePhoto) {
        this.urlProfilePhoto = urlProfilePhoto;
    }

    public String getprofessionalDegree() {
        return professionalDegree;
    }

    public void setprofessionalDegree(String professional_degree) {
        this.professionalDegree = professional_degree;
    }

    public String getidSpeciality() {
        return idSpeciality;
    }

    public void setidSpeciality(String idSpecialization) {
        this.idSpeciality = idSpecialization;
    }

    public List<WorkDay> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<WorkDay> schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "professionalDegree='" + professionalDegree + '\'' +
                ", idSpeciality='" + idSpeciality + '\'' +
                ", schedule=" + schedule +
                ", urlProfilePhoto='" + urlProfilePhoto + '\'' +
                ", gradesFeedback=" + gradesFeedback +
                ", gradeFeedback=" + gradeFeedback +
                '}';
    }
}
