package com.lzh.dbinder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lzh.dbinder.unit.IBindUnit;

/**
 * 用于bean类中绑定Field与ViewID
 *
 * Created by lzh on 2015/8/30.
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Bind {
    /**
     * 与Field相绑定的ViewID
     */
    int view() default -1;

    /**
     * 与此数据绑定的处理类
     */
	@SuppressWarnings("rawtypes")
	Class<? extends IBindUnit> clz();
}
