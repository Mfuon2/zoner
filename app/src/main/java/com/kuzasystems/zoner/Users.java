package com.kuzasystems.zoner;


import java.sql.Date;

/**
 * Created by victor on 30-Sep-18.
 */

public class Users {
 private int   id;
private String Name;
private String PhoneNumber;
private String Email;
private String Website;
private String Location;
private double Latitude;
private double Longitude;
private String Logo;
private String Username;
private String Password;
private int  Usertype;
private Date RegistrationDate;
private int  Status;
private String BusinessStatus;

    public String getBusinessStatus() {
        return BusinessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        BusinessStatus = businessStatus;
    }

    public Users(int id, String name, String phoneNumber, String email, String website, String location, double latitude, double longitude, String logo, String username, String password, int usertype, Date registrationDate, int status, String myBStatus) {
        this.id = id;
        Name = name;
        PhoneNumber = phoneNumber;
        Email = email;
        Website = website;
        Location = location;
        Latitude = latitude;
        Longitude = longitude;
        Logo = logo;
        Username = username;
        Password = password;
        Usertype = usertype;
        RegistrationDate = registrationDate;
        Status = status;
        BusinessStatus = myBStatus;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getUsertype() {
        return Usertype;
    }

    public void setUsertype(int usertype) {
        Usertype = usertype;
    }

    public Date getRegistrationDate() {
        return RegistrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        RegistrationDate = registrationDate;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
