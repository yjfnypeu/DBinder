package com.lzh.dbinder;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.view.View;

import com.lzh.dbinder.annotation.Bind;
import com.lzh.dbinder.reflect.Reflect;
import com.lzh.dbinder.reflect.ReflectException;
import com.lzh.dbinder.unit.IBindUnit;

/**
 * 数据绑定工具类。使用注解将绑定类与指定的资源ID相绑定。
 * 
 * @author lzh
 */
public class DBBindUtil {
	
	/**
     * 生成绑定对象。
     * @param rootView 根布局。要以此来获取bean中绑定注解的资源ID对应的View
     * @param clz 要生成的绑定对象的类型
     * @param bean 将要将数据绑定到绑定对象中的普通bean
     * @return 
     * @throws RuntimeException
     */
	public static <E> E bindBean(View root,Class<E> clz,Object bean) {
		E e = bind(root, clz);
		return bindBean(bean, e);
	}

	/**
	 * 将bean中的同名字段值赋给绑定对象target的对应对象中去。
	 * @param bean 含有与target绑定对象相同的字段名的变通bean类。
	 * @param target
	 * @return
	 */
	public static <E> E bindBean(Object bean, E target) {
		Map<String, Reflect> dest = Reflect.on(target).fields();
		Map<String, Reflect> src = Reflect.on(bean).fields();
		Set<Entry<String, Reflect>> entrySet = src.entrySet();
		for(Entry<String, Reflect> entry :entrySet) {
			String key = entry.getKey();
			if (!dest.containsKey(key)) {
				// 若数据绑定集中未含有此字段名，继续下次循环
				continue;
			}
			
			if (!(dest.get(key).get() instanceof IBindUnit)) {
				// 若对应的字段名非数据绑定类。继续下次循环
				continue;
			}
			Reflect destRef = dest.get(key);
			Reflect cRef = entry.getValue();
			try {
				destRef.call("doBind", cRef.get());
			} catch (ReflectException e1) {
				e1.printStackTrace();
			}
		}
		return target;
	}

    /**
     * 生成绑定对象。
     * @param rootView 根布局。要以此来获取bean中绑定注解的资源ID对应的View
     * @param clz 要生成的绑定对象的类型
     * @return 
     * @throws RuntimeException
     */
    @SuppressWarnings("rawtypes")
	public static <E> E bind(View rootView,Class<E> clz) {
    	// 绑定的Bean类需要有无参构造
    	Reflect instance = Reflect.on(clz).create();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            Bind bind = field.getAnnotation(Bind.class);
            if (bind == null) {
                continue;
            }
            int resId = bind.value();
            Class<? extends IBindUnit> unitClass = bind.clz();
            if (resId == -1 || unitClass.equals(IBindUnit.class)) {
                // 为默认值。即未设置。
                throw new RuntimeException("字段名:" + field.getName() + "中未指定资源ID或者绑定单元类别");
            }

            View v = rootView.findViewById(resId);
            if (v == null) {
                throw new RuntimeException("字段名:" + field.getName() + "对应id名的View未找到");
            }

            IBindUnit unit = Reflect.on(unitClass).create(v).get();
        	instance.set(field.getName(), unit);
        }
        return instance.get();
    }
    
}
