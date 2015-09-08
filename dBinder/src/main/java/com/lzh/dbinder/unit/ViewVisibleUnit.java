package com.lzh.dbinder.unit;

import android.view.View;

/**
 * 绑定设置View的visible属性。所需要的值与View原生一样.View.VISIBLE、View.INVISIBLE,View.GONE
 * @author Administrator
 *
 * @param <T>
 */
public class ViewVisibleUnit<T extends View> implements IBindUnit<Integer, T> {
	
	private T mView;
	private Integer visible;

	public ViewVisibleUnit (T v) {
		doBindView(v);
	}
	
	@Override
	public Integer unBindData() {
		return visible;
	}

	@Override
	public void doBind(Integer e) {
		this.visible = e;
		mView.setVisibility(e);
	}

	@Override
	public T unBindView() {
		return mView;
	}

	@Override
	public void doBindView(T t) {
		this.mView = t;
	}
	
}
