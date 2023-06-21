package com.example.smiletogether_dentalapp.Model;

public class Investigation {
    private String name="";
    private double price;

    public Investigation() {
    }

    public Investigation(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public double getprice() {
        return price;
    }

    public void setprice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Investigation{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
