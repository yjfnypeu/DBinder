package com.lzh.dbinder.annotation;

import com.lzh.dbinder.unit.IBindUnit;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于bean类中绑定Field与ViewID
 *
 * Created by lzh on 2015/8/30.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Bind {
    /**
     * 与Field相绑定的ViewID
     */
    int value() default -1;

    /**
     * 与此数据绑定的处理类
     */
	@SuppressWarnings("rawtypes")
	Class<? extends IBindUnit> clz() default IBindUnit.class;
}

