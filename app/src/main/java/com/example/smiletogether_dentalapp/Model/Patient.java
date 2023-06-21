package com.example.smiletogether_dentalapp.Model;

public class Patient extends User{
    private String address;
    private String urlProfilePhoto;
    private boolean deletedAccount;

    public Patient() {
    }

    public Patient(String id, String lastname, String firstname, String email, String nrPhone, String password, String role, String sex, String dateOfBirth, String address,String urlProfilePhoto) {
        super(id, lastname, firstname, email, nrPhone, password, role, sex, dateOfBirth);
        this.address = address;
        this.urlProfilePhoto=urlProfilePhoto;
        this.deletedAccount=false;
    }

    public boolean isDeletedAccount() {
        return deletedAccount;
    }

    public void setDeletedAccount(boolean deletedAccount) {
        this.deletedAccount = deletedAccount;
    }

    public String getUrlProfilePhoto() {
        return urlProfilePhoto;
    }

    public void setUrlProfilePhoto(String urlProfilePhoto) {
        this.urlProfilePhoto = urlProfilePhoto;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "address='" + address + '\'' +
                '}';
    }
}
