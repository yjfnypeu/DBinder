package org.lzh.dbinderdemo.bind;

import android.widget.ListView;
import android.widget.TextView;

import com.lzh.dbinder.annotation.Bind;
import com.lzh.dbinder.unit.IBindUnit;
import com.lzh.dbinder.unit.ListUnit;
import com.lzh.dbinder.unit.TVUnit;

import org.lzh.dbinderdemo.R;
import org.lzh.dbinderdemo.bean.ItemInfo;

/**
 * Created by Administrator on 2015/9/7.
 */
public class UserInfo {
    @Bind(value = R.id.username,clz = TVUnit.class)
    private IBindUnit<String,TextView> username;
    @Bind(value = R.id.password,clz = TVUnit.class)
    private IBindUnit<String, TextView> password;
    @Bind(value = R.id.lv,clz = ListUnit.class)
    private ListUnit<ItemInfo,ListView> lv;

    public IBindUnit<String, TextView> getUsername() {
        return username;
    }

    public void setUsername(IBindUnit<String, TextView> username) {
        this.username = username;
    }

    public IBindUnit<String, TextView> getPassword() {
        return password;
    }

    public void setPassword(IBindUnit<String, TextView> password) {
        this.password = password;
    }

    public ListUnit<ItemInfo, ListView> getLv() {
        return lv;
    }

    public void setLv(ListUnit<ItemInfo, ListView> lv) {
        this.lv = lv;
    }
}
