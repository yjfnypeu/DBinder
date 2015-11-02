package org.lzh.dbindertest.bean;

import com.lzh.dbinder.annotation.Bind;
import com.lzh.dbinder.unit.TVUnit;

import org.lzh.dbindertest.R;

/**
 * Created by Administrator on 2015/10/29.
 */
public class User {

    @Bind(view = R.id.username,clz = TVUnit.class)
    private String username;
    @Bind(view = R.id.password,clz = TVUnit.class)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String pwd) {
        this.password = pwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }
}
