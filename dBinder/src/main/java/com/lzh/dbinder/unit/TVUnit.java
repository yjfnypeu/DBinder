package com.lzh.dbinder.unit;

import android.view.View;
import android.widget.TextView;

/**
 * 绑定TextView与Value，直接设置setText
 * Created by lzh on 2015/8/30.
 */
public class TVUnit implements IBindUnit<String>{

    private TextView tv;

    public TVUnit(View e) {
        doBindView(e);
    }

    @Override
    public String unBindData() {
        return tv.getText().toString();
    }

    @Override
    public void doBind(String data) {
        tv.setText(data);
    }

	@Override
	public TextView unBindView() {
		return tv;
	}

	@Override
	public void doBindView(View t) {
		this.tv = (TextView) t;
	}

    @Override
    public void detach() {
        this.tv = null;
    }
}
