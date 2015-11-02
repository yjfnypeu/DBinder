package com.lzh.dbinder.unit;

import android.view.View;

public class ViewVisibleUnit implements IBindUnit<Integer> {
	
	private View mView;
	private Integer visible;

	public ViewVisibleUnit (View v) {
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
	public View unBindView() {
		return mView;
	}

	@Override
	public void doBindView(View t) {
		this.mView = t;
	}

	@Override
	public void detach() {
		mView = null;
	}

}
