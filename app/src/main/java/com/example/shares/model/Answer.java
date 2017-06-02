package com.example.shares.model;

import android.graphics.Bitmap;

import cn.bmob.v3.BmobObject;

/**
 * Created by hoem- on 2017/5/16.
 */

public class Answer extends BmobObject {
    private String Content;
    private Topic topic;
    private String publishTime;
    private Bitmap avater;
    private String aPath;
    private MyUser author;//评论人
    private MyUser reply;//给谁回复

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private Integer answerNum;

    public Integer getAnswerNum() {
        return answerNum;
    }

    public String getNickName() {
        return nickName;
    }

    private String nickName;
    public MyUser getReply() {
        return reply;
    }

    public void setReply(MyUser reply) {
        this.reply = reply;
    }

    public String getContent() {
        return Content;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setAvater(Bitmap avater) {
        this.avater = avater;
    }

    public void setaPath(String aPath) {
        this.aPath = aPath;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public Bitmap getAvater() {
        return avater;
    }

    public String getaPath() {
        return aPath;
    }
}