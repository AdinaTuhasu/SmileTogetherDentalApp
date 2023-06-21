package com.example.smiletogether_dentalapp.Model;

import java.util.ArrayList;
import java.util.List;

public class Speciality {
    private String idSpeciality="";
    private String name="";
    private List<Investigation> investigations=new ArrayList<>();

    public Speciality() {
    }

    public Speciality(String idSpeciality, String name, List<Investigation> investigations) {
        this.idSpeciality = idSpeciality;
        this.name = name;
        this.investigations = investigations;
    }

    public String getidSpeciality() {
        return idSpeciality;
    }

    public void setidSpeciality(String idSpeciality) {
        this.idSpeciality = idSpeciality;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public List<Investigation> getInvestigations() {
        return investigations;
    }

    public void setInvestigations(List<Investigation> investigations) {
        this.investigations = investigations;
    }

    @Override
    public String toString() {
        return "Specialization{" +
                "idSpecialization='" + idSpeciality+ '\'' +
                ", name='" + name + '\'' +
                ", investigations=" + investigations +
                '}';
    }
}
