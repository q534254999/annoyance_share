package com.example.shares.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Wenpzhou on 2017/5/20 0020.
 */

public class Error extends BmobObject {
    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Error(String path) {

        this.path = path;
    }

    public Error(String tableName, String path) {
        super(tableName);
        this.path = path;
    }
}
