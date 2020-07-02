package com.aaptrix.databeans;

import java.io.Serializable;

/**
 * Created by Administrator on 1/1/2018.
 */

public class DataBeanActivities implements Serializable
{
    String activiId,activiTitle,activiDesc,activiDate,activiImg,activiImg2,activiImg3,activiImg4, batch;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getActiviId() {
        return activiId;
    }

    public void setActiviId(String activiId) {
        this.activiId = activiId;
    }

    public String getActiviTitle() {
        return activiTitle;
    }

    public void setActiviTitle(String activiTitle) {
        this.activiTitle = activiTitle;
    }

    public String getActiviDesc() {
        return activiDesc;
    }

    public void setActiviDesc(String activiDesc) {
        this.activiDesc = activiDesc;
    }

    public String getActiviDate() {
        return activiDate;
    }

    public void setActiviDate(String activiDate) {
        this.activiDate = activiDate;
    }

    public String getActiviImg() {
        return activiImg;
    }

    public void setActiviImg(String activiImg) {
        this.activiImg = activiImg;
    }

    public String getActiviImg2() {
        return activiImg2;
    }

    public void setActiviImg2(String activiImg2) {
        this.activiImg2 = activiImg2;
    }

    public String getActiviImg3() {
        return activiImg3;
    }

    public void setActiviImg3(String activiImg3) {
        this.activiImg3 = activiImg3;
    }

    public String getActiviImg4() {
        return activiImg4;
    }

    public void setActiviImg4(String activiImg4) {
        this.activiImg4 = activiImg4;
    }
}
