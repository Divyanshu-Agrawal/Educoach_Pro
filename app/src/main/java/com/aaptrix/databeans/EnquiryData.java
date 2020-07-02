package com.aaptrix.databeans;

import java.io.Serializable;

public class EnquiryData implements Serializable {

    private String name, phone, email, course, details, howto, date, howtoOther;

    public String getHowtoOther() {
        return howtoOther;
    }

    public void setHowtoOther(String howtoOther) {
        this.howtoOther = howtoOther;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getHowto() {
        return howto;
    }

    public void setHowto(String howto) {
        this.howto = howto;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
