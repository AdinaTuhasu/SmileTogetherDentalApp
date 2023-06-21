package com.example.smiletogether_dentalapp.Model;

public class Appointment {
    private String idAppointment;
    private String doctorsId;
    private String patientsId;
    private String date;
    private String hour;
    private String status;
    private Feedback feedback;
    private String urlPrescription;

    public Appointment() {
    }

    public Appointment(String idAppointment, String doctorsId, String patientsId, String date, String hour, String status, Feedback feedback, String urlPrescription) {
        this.idAppointment = idAppointment;
        this.doctorsId = doctorsId;
        this.patientsId = patientsId;
        this.date = date;
        this.hour = hour;
        this.status = status;
        this.feedback=feedback;
        this.urlPrescription=urlPrescription;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public String getUrlPrescription() {
        return urlPrescription;
    }

    public void setUrlPrescription(String urlPrescription) {
        this.urlPrescription = urlPrescription;
    }

    public String getIdAppointment() {
        return idAppointment;
    }

    public void setIdAppointment(String idAppointment) {
        this.idAppointment = idAppointment;
    }

    public String getDoctorsId() {
        return doctorsId;
    }

    public void setDoctorsId(String doctorsId) {
        this.doctorsId = doctorsId;
    }

    public String getPatientsId() {
        return patientsId;
    }

    public void setPatientsId(String patientsId) {
        this.patientsId = patientsId;
    }


    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public String gethour() {
        return hour;
    }

    public void sethour(String hour) {
        this.hour = hour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "IdAppointment='" + idAppointment + '\'' +
                ", IdDoctor='" + doctorsId + '\'' +
                ", IdPatient='" + patientsId + '\'' +
                ", Date='" + date + '\'' +
                ", Hour='" + hour + '\'' +
                ", Status='" + status + '\'' +
                '}';
    }
}
