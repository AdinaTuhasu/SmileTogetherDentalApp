package com.example.smiletogether_dentalapp.Model;

public class Notification {
    private String idNotification;
    private String title;
    private String idTransmitter;
    private String idReceiver;
    private String appointmentDate;
    private String appointmentTime;
    private String date;
    private boolean noticeRead;

    public Notification() {
    }

    public Notification(String idNotification, String title, String idTransmitter, String idReceiver, String appointmentDate, String appointmentTime, String date, boolean noticeRead) {
        this.idNotification = idNotification;
        this.title = title;
        this.idTransmitter = idTransmitter;
        this.idReceiver = idReceiver;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.date = date;
        this.noticeRead = noticeRead;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdTransmitter() {
        return idTransmitter;
    }

    public void setIdTransmitter(String idTransmitter) {
        this.idTransmitter = idTransmitter;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isNoticeRead() {
        return noticeRead;
    }

    public void setNoticeRead(boolean noticeRead) {
        this.noticeRead = noticeRead;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "idNotification='" + idNotification + '\'' +
                ", title='" + title + '\'' +
                ", idTransmitter='" + idTransmitter + '\'' +
                ", idReceiver='" + idReceiver + '\'' +
                ", AppointmentDate='" + appointmentDate + '\'' +
                ", AppointmentTime='" + appointmentTime + '\'' +
                ", date='" + date + '\'' +
                ", noticeRead=" + noticeRead +
                '}';
    }
}
