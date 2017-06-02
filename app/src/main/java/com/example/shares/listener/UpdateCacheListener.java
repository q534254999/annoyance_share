package com.example.shares.listener;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;

/**
 * @author :
 * @project:UpdateCacheListener
 * @date :
 */
public abstract class UpdateCacheListener extends BmobListener {
    public abstract void done(BmobException e);

    @Override
    protected void postDone(Object o, BmobException e) {
        done(e);
    }
}
