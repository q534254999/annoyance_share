package com.example.shares.listener;

import com.example.shares.model.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;

/**
 * @author :
 * @project:QueryUserListener
 * @date :
 */
public abstract class QueryUserListener extends BmobListener<MyUser> {

    public abstract void done(MyUser s, BmobException e);

    @Override
    protected void postDone(MyUser o, BmobException e) {
        done(o, e);
    }
}
