package com.example.shares.model;

import android.graphics.Bitmap;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Wenpzhou on 2017/4/8 0008.
 * 发布烦恼的单元信息
 */

public class Annoyance extends BmobObject implements Serializable{
    private String nickName;
    private MyUser author;
    private String contentTitle;
    private String contentText;
    private Integer answerNum;
    private String publishTime;
    private Bitmap avater;
    private String aPath;
    private Boolean isAnonymous = Boolean.FALSE;

    public Annoyance(){

    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getaPath() {
        return aPath;
    }

    public void setaPath(String aPath) {
        this.aPath = aPath;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }
}
