package com.lzh.dbinder.unit;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * 绑定ImageView
 * 
 * @author lzh
 *
 * @param <E> 绑定的View类型
 * @param <T> 绑定至此View的数据类型
 */
public class ImageUnit<E extends ImageView, T> implements IBindUnit<T,E>{

    private ImageView img;
    private T data;

    public ImageUnit(E img) {
    	doBindView(img);
    }

    public void doBind(Integer integer) {
        img.setImageResource(integer);
    }

    public void doBind(Bitmap bitmap) {
        img.setImageBitmap(bitmap);
    }

    public void doBind(Drawable drawable) {
        img.setImageDrawable(drawable);
    }

    @Override
    public T unBindData() {
        return data;
    }

    @Override
    public void doBind(T o) {
        this.data = o;
        if (o instanceof Integer) {
            doBind((Integer)o);
        } else if (o instanceof Bitmap) {
            doBind((Bitmap)o);
        } else if (o instanceof Drawable) {
            doBind((Drawable)o);
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public E unBindView() {
		return (E) img;
	}

	@Override
	public void doBindView(E t) {
		this.img = t;
	}

}
