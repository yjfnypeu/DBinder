package com.lzh.dbinder.unit;

import android.view.View;
/**
 * 数据绑定所需要实现的接口。
 * @author lzh
 *
 * @param <E> 绑定数据的泛型
 * @param <T> 要绑定的View的类型
 */
public interface IBindUnit<E,T extends View> {
    /**
     * @return 返回与此View绑定的数据
     */
    E unBindData();
    /**
     * @param e 绑定数据e到View
     */
    void doBind(E e);
    
    /**
     * @return 返回绑定的View
     */
    T unBindView();
	/**
	 * @param t 绑定View
	 */
	void doBindView(T t);
}
