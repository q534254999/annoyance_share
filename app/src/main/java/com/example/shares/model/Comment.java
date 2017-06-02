package com.example.shares.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Wenpzhou on 2017/5/7 0007.
 */

public class Comment extends BmobObject {
    private String Content;
    private Annoyance annoyance;
    private MyUser author;//评论人
    private MyUser reply;//给谁回复

    public MyUser getReply() {
        return reply;
    }

    public void setReply(MyUser reply) {
        this.reply = reply;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Annoyance getAnnoyance() {
        return annoyance;
    }

    public void setAnnoyance(Annoyance annoyance) {
        this.annoyance = annoyance;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }
}
