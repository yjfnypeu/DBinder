package com.lzh.dbinder.unit;

import android.widget.TextView;

/**
 * 绑定TextView与Value，直接设置setText
 * Created by lzh on 2015/8/30.
 */
public class TVUnit<T extends TextView> implements IBindUnit<String,T>{

    private T tv;
    private String data;

    public TVUnit(T e) {
    	doBindView(e);
    }

    @Override
    public String unBindData() {
        return data;
    }

    @Override
    public void doBind(String data) {
        this.data = data;
        tv.setText(data);
    }

	@Override
	public T unBindView() {
		return tv;
	}

	@Override
	public void doBindView(T t) {
		this.tv = t;
	}
}
