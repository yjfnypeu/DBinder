package com.lzh.dbinder.unit;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by lzh on 2015/8/30.
 */
public class ImageUnit implements IBindUnit<Object>{

    private ImageView img;
    private Object data;

    public ImageUnit(View img) {
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
    public Object unBindData() {
        return data;
    }

    @Override
    public void doBind(Object o) {
        this.data = o;
        if (o instanceof Integer) {
            doBind((Integer)o);
        } else if (o instanceof Bitmap) {
            doBind((Bitmap)o);
        } else if (o instanceof Drawable) {
            doBind((Drawable)o);
        } else {
            throw new RuntimeException("The bind type must be instance of Integer/Bitmap/Drawable");
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public ImageView unBindView() {
		return (ImageView) img;
	}

	@Override
	public void doBindView(View t) {
		this.img = (ImageView) t;
	}

    @Override
    public void detach() {
        this.img = null;
        this.data = null;
    }

}
