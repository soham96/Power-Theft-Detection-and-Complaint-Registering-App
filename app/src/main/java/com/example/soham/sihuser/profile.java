package com.example.soham.sihuser;

/**
 * Created by Soham on 25-03-2017.
 */

public class profile {

    private String Aadhar;
    private String Email;
    private String Image;
    private String Name;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    private String Address;

    public profile()
    {}

    public profile(String aadhar, String email, String image, String name, String address) {
        Aadhar = aadhar;
        Email = email;
        Image = image;
        Name = name;
        Address = address;
    }



    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAadhar() {
        return Aadhar;
    }

    public void setAadhar(String aadhar) {
        Aadhar = aadhar;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


}
