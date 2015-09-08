package com.lzh.dbinder.unit;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 绑定设置View的background
 * @author lzh
 *
 * @param <E>
 */
public class ViewBgUnit<E extends View> implements IBindUnit<Object, E> {

	private E e;
	private Object obj;
	
	public ViewBgUnit(E e) {
		doBindView(e);
	}
	
	@Override
	public Object unBindData() {
		return obj;
	}
	
	private void doBindInteger(Integer integer) {
		e.setBackgroundResource(integer);
	}
	
	@SuppressWarnings("deprecation")
	private void doBindBitmap(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		doBindDrawable(bd);
	}
	
	@SuppressWarnings("deprecation")
	private void doBindDrawable(Drawable drawable) {
		e.setBackgroundDrawable(drawable);
	}
	
	public void doBindColor(Integer color) {
		this.obj = color;
		e.setBackgroundColor(color);
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
		}
	}

	@Override
	public E unBindView() {
		return e;
	}

	@Override
	public void doBindView(E t) {
		this.e = t;
	}

}
