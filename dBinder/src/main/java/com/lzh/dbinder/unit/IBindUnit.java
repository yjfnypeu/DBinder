package com.lzh.dbinder.unit;

import android.view.View;

/**
 * Created by lzh on 2015/8/30.
 */
public interface IBindUnit<E> {
    E unBindData();
    void doBind(E e);
    
    View unBindView();
	void doBindView(View v);

    void detach();
}
