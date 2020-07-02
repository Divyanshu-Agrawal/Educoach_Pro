package com.aaptrix.databeans;

import java.io.Serializable;

public class QuestionData implements Serializable {

    private String ques, quesImg, optionA, optionAImg, optionB, optionBImg, optionC, optionCImg, optionD, optionDImg, quesId, correctOption;
    private String tbl_que_id, tbl_user_answer, status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTbl_que_id() {
        return tbl_que_id;
    }

    public void setTbl_que_id(String tbl_que_id) {
        this.tbl_que_id = tbl_que_id;
    }

    public String getTbl_user_answer() {
        return tbl_user_answer;
    }

    public void setTbl_user_answer(String tbl_user_answer) {
        this.tbl_user_answer = tbl_user_answer;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public String getQuesImg() {
        return quesImg;
    }

    public void setQuesImg(String quesImg) {
        this.quesImg = quesImg;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionAImg() {
        return optionAImg;
    }

    public void setOptionAImg(String optionAImg) {
        this.optionAImg = optionAImg;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionBImg() {
        return optionBImg;
    }

    public void setOptionBImg(String optionBImg) {
        this.optionBImg = optionBImg;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionCImg() {
        return optionCImg;
    }

    public void setOptionCImg(String optionCImg) {
        this.optionCImg = optionCImg;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getOptionDImg() {
        return optionDImg;
    }

    public void setOptionDImg(String optionDImg) {
        this.optionDImg = optionDImg;
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }
}
