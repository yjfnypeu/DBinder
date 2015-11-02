package com.lzh.dbinder.unit;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ViewBgUnit implements IBindUnit<Object> {

	private View v;
	private Object obj;
	
	public ViewBgUnit(View e) {
		doBindView(e);
	}
	
	@Override
	public Object unBindData() {
		return obj;
	}
	
	private void doBindInteger(Integer integer) {
		v.setBackgroundResource(integer);
	}
	
	@SuppressWarnings("deprecation")
	private void doBindBitmap(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		doBindDrawable(bd);
	}
	
	@SuppressWarnings("deprecation")
	private void doBindDrawable(Drawable drawable) {
		v.setBackgroundDrawable(drawable);
	}
	
	public void doBindColor(Integer color) {
		this.obj = color;
		v.setBackgroundColor(color);
	}
	
	@Override
	public void doBind(Object e) {
		this.obj = e;
		if (e instanceof Integer) {
			doBindInteger((Integer)e);
		} else if (e instanceof Bitmap) {
			doBindBitmap((Bitmap) e);
		} else if (e instanceof Drawable) {
			doBindDrawable((Drawable) e);
		} else {
			throw new RuntimeException("The bind type must be instance of Integer/Bitmap/Drawable");
		}
	}

	@Override
	public View unBindView() {
		return v;
	}

	@Override
	public void doBindView(View t) {
		this.v = t;
	}

	@Override
	public void detach() {
		this.v = null;
	}

}
