package com.example.shares.model;

import android.graphics.Bitmap;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by hoem- on 2017/5/12.
 */

public class Topic extends BmobObject implements Serializable{

    private String contentTitle;
    private String contentText;
    private Integer answerNum;
    private Bitmap avater;
    private String aPath;
    private Integer answernum;
    public Topic(){

    }


    public Integer getAnswernum() {
        return answernum;
    }

    public void setAnswernum(Integer answernum) {
        this.answernum = answernum;
    }

    public Bitmap getAvater() {
        return avater;
    }

    public void setAvater(Bitmap avater) {
        this.avater = avater;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }



    public String getaPath() {
        return aPath;
    }

    public void setaPath(String aPath) {
        this.aPath = aPath;
    }
}
