package com.aaptrix.databeans;

import java.io.Serializable;

public class OnlineExamData implements Serializable {

    private String id;
    private String name;
    private String date;
    private String course;
    private String subject;
    private String duration;
    private String startTime;
    private String endTime;
    private String marks;
    private String negMarks;
    private String resPublish;
    private String endDate;
    private String type, quesPdf, ansPdf;

    public String getQuesPdf() {
        return quesPdf;
    }

    public void setQuesPdf(String quesPdf) {
        this.quesPdf = quesPdf;
    }

    public String getAnsPdf() {
        return ansPdf;
    }

    public void setAnsPdf(String ansPdf) {
        this.ansPdf = ansPdf;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getResPublish() {
        return resPublish;
    }

    public void setResPublish(String resPublish) {
        this.resPublish = resPublish;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getNegMarks() {
        return negMarks;
    }

    public void setNegMarks(String negMarks) {
        this.negMarks = negMarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
