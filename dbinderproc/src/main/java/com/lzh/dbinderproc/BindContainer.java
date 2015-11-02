package com.lzh.dbinderproc;

import com.lzh.dbinder.annotation.Bind;
import com.lzh.dbinder.reflect.Reflect;
import com.lzh.dbinderproc.util.FileLog;
import com.lzh.dbinderproc.util.StringUtils;
import com.lzh.dbinderproc.util.UtilMgr;
import com.lzh.dbinderproc.util.ValidUtil;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 绑定所需的数据容器
 */ 
public class BindContainer {

	private static final String SUFFIX = "_Bind";
	private TypeElement clzElement;// 类名

	private List<EleHolder> fieldsList = null;
	/**
	 * 实例化BindContainer。并对子Element进行简单有效性过滤判断。<br>
	 * FIELD:<br>
	 * 		<li>若有Bind注解的。检查是否bind.view()与bind.clz()均正确定义<br>
	 * 		<li>字段修饰符需要无final/static/<br>
	 * CONSTRUCTOR:<br>
	 * 		<li>需要有一个无参构造<br>
	 * 		<li>需要修饰符含public/protect,无final<br>
	 * METHOD:<br>
	 * 		<li>无native/static/修饰符。有is/get/set前缀
	 * @param clzElement
	 * @throws Exception 
	 */
	public BindContainer(TypeElement clzElement) throws Exception {
		this.clzElement = clzElement;
		fieldsList = new ArrayList<EleHolder>();
		List<? extends Element> elements = this.clzElement.getEnclosedElements();
		Types typeUtils = UtilMgr.getMgr().getTypeUtils();
		boolean hasEmptyContructor = false;
		for (int i = 0; i < elements.size(); i++) {
			EleHolder holder = new EleHolder();
			Element ele = elements.get(i);
			switch (ele.getKind()) {
			case FIELD:
				if (ValidUtil.hasBindAnno(ele)) {
					ValidUtil.checkBindAnnotationValid((VariableElement) ele);
					holder.fieldElement = ele;
					Bind bind = ele.getAnnotation(Bind.class);
					holder.viewId = bind.view();
					holder.unitClzName = ValidUtil.getClzNameByBind(bind);
					holder.name = ele.getSimpleName().toString();
					break;
				}
				
//				TypeElement typeElement = (TypeElement) typeUtils.asElement(ValidUtil.boxed(ele.asType()));
//				if (ValidUtil.hasSuperBindAnno(typeElement)) {
//					if (ValidUtil.isFieldElementValid(ele)) {
//						holder.fieldElement = ele;
//						holder.isBind = false;
//						holder.hasNext = true;
//						holder.name = ele.getSimpleName().toString();
//						break;
//					} else {
//						throw new ProcException(ele, "The field %s must be modified by non-final/static", ele.getSimpleName());
//					}
//				}
				continue;
			case CONSTRUCTOR:
				if (ValidUtil.isConstrElementValid(ele)) {
					hasEmptyContructor = true;
				}
				continue;
			default:
				continue;
			}
			
			fieldsList.add(holder);
		}
		
		if (!hasEmptyContructor) {
			throw new ProcException(clzElement,
				"class %s should has a public non-final and empty constructor", clzElement.getSimpleName());
		}
		
		for(EleHolder holder : fieldsList) {
			Element fieldElement = holder.fieldElement;
			String fieldName = fieldElement.getSimpleName().toString();
			String getMethod = StringUtils.getGetMethodName(fieldName);
			String setMethod = StringUtils.getSetMethodName(fieldName);
			String isMethod = StringUtils.getIsMethodName(fieldName);
			FileLog.print("The field %s set/get/is method:%s\t%s\t%s", fieldName, setMethod, getMethod, isMethod);
			
			Element getElement = ValidUtil.getElementByName(getMethod, ElementKind.METHOD, clzElement);
			Element setElement = ValidUtil.getElementByName(setMethod, ElementKind.METHOD, clzElement);
			if (setElement == null && ValidUtil.isBaseType(fieldElement.asType())) {
				setElement = ValidUtil.getElementByName(isMethod, ElementKind.METHOD, clzElement);
			}
			
			if (getElement == null || setElement == null) {
				throw new ProcException(fieldElement, "The field %s must have set/get method", fieldElement.getSimpleName());
			}
			
			ValidUtil.checkGetMethodValid((ExecutableElement)getElement, (VariableElement)fieldElement);
			ValidUtil.checkSetMethodValid((ExecutableElement)setElement, (VariableElement)fieldElement);
			
			holder.getElement = getElement;
			holder.setElement = setElement;
		}
		
		FileLog.print("BindContainer parse complete!fieldsMap:%s", fieldsList);
		FileLog.print("=================divider line==================");
	}
	
	class EleHolder {
		String name;//字段或方法名
		Element fieldElement;//字段
		Element setElement;// 与字段对应的set方法
		Element getElement;// 与字段对应的get方法
		int viewId;// 绑定单元上的viewId
		String unitClzName;// 绑定单元类名
//		boolean isBind;// 是否含有bind注解
//		boolean hasNext;// 是否含有子集
		@Override
		public String toString() {
			return "EleHolder [name=" + name + ", fieldElement=" + fieldElement
					+ ", setElement=" + setElement + ", getElement="
					+ getElement + ", viewId=" + viewId + ", unitClzName="
					+ unitClzName + ", isBind=" + "]";
		}
		
	}
	
	void generateCode() throws Exception {
		Elements elementUtils = UtilMgr.getMgr().getElementUtils();
		Filer filer = UtilMgr.getMgr().getFiler();
		String bindClzName = clzElement.getSimpleName().toString() + SUFFIX;
		FileLog.print("BindClzName of %s:%s", clzElement.getSimpleName(),bindClzName);
		PackageElement pkg = elementUtils.getPackageOf(clzElement);
		String pkgName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();
		FileLog.print("pkgName:%s", pkgName);

		TypeName superClz = TypeName.get(clzElement.asType());
		TypeSpec.Builder clzBuilder = TypeSpec.classBuilder(bindClzName)
				.addModifiers(Modifier.PUBLIC).superclass(superClz);
		FieldSpec.Builder rootViewBuilder = FieldSpec.builder(getType("android.view.View"),"rootView")
				.addModifiers(Modifier.PRIVATE);
		clzBuilder.addField(rootViewBuilder.build());
		for (EleHolder holder : fieldsList) {
			generateGetSetMethod(elementUtils, clzBuilder, holder);
		}

		// generate attach method
		generateAttachMethod(elementUtils, bindClzName, clzBuilder);

		// generate detach method
		generateDetachMethod(clzBuilder,bindClzName);

		// generate cast to bean method
		generateCastBeanMethod(bindClzName, clzBuilder);

		// generate re-bind method
		String paramsView = "root";
		String paramsBean = clzElement.getSimpleName().toString().toLowerCase();
		MethodSpec.Builder rebindMethodBuilder = MethodSpec.methodBuilder("rebind")
				.addModifiers(Modifier.STATIC,Modifier.PUBLIC)
				.addParameter(getType("android.view.View"), paramsView)
				.addParameter(TypeName.get(clzElement.asType()),paramsBean)
				.returns(TypeName.get(clzElement.asType()));

		rebindMethodBuilder.beginControlFlow(format("if (%s == null || %s == null)",paramsView,paramsBean))
				.addStatement(format("throw new RuntimeException(\"%s\")","root view or bean is null")).endControlFlow();

		String bindName = "bind";
		String beanName = "bean";
		rebindMethodBuilder.addStatement(format("%s %s = null", clzElement.getSimpleName(), beanName));
		rebindMethodBuilder.addStatement(format("%s %s = null", bindClzName, bindName));
		rebindMethodBuilder.beginControlFlow(format("if (%s instanceof %s)",paramsBean,bindClzName))
				.addStatement(format("%s = castToBean(%s,%s)",beanName,paramsView,paramsBean))
				.addStatement(format("%s = (%s)%s",bindName,bindClzName,paramsBean))
				.endControlFlow()
				.beginControlFlow("else")
				.addStatement(format("%s = %s",beanName,paramsBean))
				.endControlFlow()
				.beginControlFlow(format("if (%s == null || %s.rootView != %s)",bindName,bindName,paramsView))
				.addStatement(format("%s = (%s)attach(%s)",bindName,bindClzName,paramsView))
				.endControlFlow();

		for(EleHolder holder : fieldsList) {
			rebindMethodBuilder.addStatement(format("%s.%s(%s.%s())",bindName,holder.setElement.getSimpleName(),beanName,holder.getElement.getSimpleName()));
		}

		rebindMethodBuilder.addStatement(format("return %s",bindName));
		clzBuilder.addMethod(rebindMethodBuilder.build());

		JavaFile.Builder javaBuilder = JavaFile.builder(pkgName, clzBuilder.build());
		javaBuilder.addFileComment("The file is auto-generate,do not modify!");
		javaBuilder.build().writeTo(filer);
	}

	private void generateCastBeanMethod(String bindClzName, TypeSpec.Builder clzBuilder) {
		String paramsView = "root";
		String paramsBean = clzElement.getSimpleName().toString().toLowerCase();
		MethodSpec.Builder castBeanMethodBuilder = MethodSpec.methodBuilder("castToBean")
				.addModifiers(Modifier.STATIC,Modifier.PUBLIC)
				.addParameter(getType("android.view.View"),paramsView)
				.addParameter(TypeName.get(clzElement.asType()),paramsBean)
				.returns(TypeName.get(clzElement.asType()));

		castBeanMethodBuilder.beginControlFlow(format("if(%s == null)",paramsView))
				.addStatement(format("throw new RuntimeException(%s)","\"The View of root is null\"")).endControlFlow();

		castBeanMethodBuilder.beginControlFlow(format("if (%s == null || !(%s instanceof %s))",paramsBean,paramsBean,bindClzName))
				.addStatement(format("return %s",paramsBean))
				.endControlFlow();

		castBeanMethodBuilder.addStatement(format("%s bean = new %s()", clzElement.getSimpleName(), clzElement.getSimpleName()));
		for(EleHolder holder : fieldsList) {
			castBeanMethodBuilder.addStatement(
					format("bean.%s(%s.%s())",holder.setElement.getSimpleName(),paramsBean,holder.getElement.getSimpleName()));
		}

		castBeanMethodBuilder.addStatement(format("return %s",paramsBean));
		clzBuilder.addMethod(castBeanMethodBuilder.build());
	}

	/**
	 * 生成detach方法
	 */
	private void generateDetachMethod(TypeSpec.Builder clzBuilder,String clzName) {
		String params = clzElement.getSimpleName().toString().toLowerCase();
		MethodSpec.Builder detachMethodBuidler = MethodSpec.methodBuilder("detach")
				.addParameter(TypeName.get(clzElement.asType()),params)
				.addModifiers(Modifier.PUBLIC,Modifier.STATIC)
				.returns(TypeName.VOID);
		detachMethodBuidler.beginControlFlow(format("if (%s == null || !(%s instanceof %s))",params,params,clzName))
				.addStatement("return")
				.endControlFlow();
		detachMethodBuidler.addStatement(format("%s instance = (%s) %s",clzName,clzName,params));
		for (EleHolder holder : fieldsList) {
			detachMethodBuidler.beginControlFlow(String.format("if(instance.%s != null)",holder.name))
					.addStatement(format("instance.%s.detach()",holder.name))
					.addStatement(format("instance.%s = null",holder.name))
					.endControlFlow();
		}
		clzBuilder.addMethod(detachMethodBuidler.build());
	}
	/**
	 * 生成attach方法
	 */
	private void generateAttachMethod(Elements elementUtils, String bindClzName, TypeSpec.Builder clzBuilder) {
		MethodSpec.Builder attachMethodBuilder = MethodSpec.methodBuilder("attach")
				.addModifiers(Modifier.PUBLIC,Modifier.STATIC)
				.addParameter(getType("android.view.View"),"root")
				.returns(TypeName.get(clzElement.asType()));

		attachMethodBuilder.addStatement(bindClzName + " instance = new " + bindClzName + "()");
		attachMethodBuilder.addStatement(format("instance.rootView = root"));
		for (EleHolder holder : fieldsList) {
			String bindFieldName = holder.unitClzName;
			TypeElement typeElement = elementUtils.getTypeElement(bindFieldName);
			TypeName name = Reflect.on(TypeName.class).create(bindFieldName).get();
			attachMethodBuilder.addStatement("instance." + holder.name + " = new " + holder.unitClzName + "(root.findViewById("+holder.viewId +"))");
		}

		attachMethodBuilder.addStatement("return instance");

		clzBuilder.addMethod(attachMethodBuilder.build());
	}

	/**
	 * 生成get/set与Unit的成员变量
	 */
	private void generateGetSetMethod(Elements elementUtils, TypeSpec.Builder clzBuilder, EleHolder holder) {
		String bindFieldName = holder.unitClzName;
		TypeElement typeElement = elementUtils.getTypeElement(bindFieldName);
		TypeName name = Reflect.on(TypeName.class).create(bindFieldName).get();
		FieldSpec.Builder builder = FieldSpec.builder(name, holder.name, Modifier.PRIVATE);
		clzBuilder.addField(builder.build());
		// generate get method
		MethodSpec.Builder getMethodBuilder = MethodSpec.overriding((ExecutableElement) holder.getElement);

		getMethodBuilder.addStatement("return " + holder.name + ".unBindData()");
		clzBuilder.addMethod(getMethodBuilder.build());

		// generate set method
		ExecutableElement superSetMethod = (ExecutableElement) holder.setElement;
		MethodSpec.Builder setMethodBuilder = MethodSpec.overriding(superSetMethod);
		String paramsName = superSetMethod.getParameters().get(0).getSimpleName().toString();
		setMethodBuilder.addStatement("this." + holder.name + ".doBind(" + paramsName + ")");
		setMethodBuilder.addStatement("super." + holder.setElement.getSimpleName().toString() + "(" + paramsName + ")");

		clzBuilder.addMethod(setMethodBuilder.build());

		// generate get view method
		MethodSpec.Builder getUnitMethodBuidler = MethodSpec
                .methodBuilder(StringUtils.getGetMethodName(holder.name) + "Unit")
                .addModifiers(Modifier.PUBLIC)
                .returns(name);

		getUnitMethodBuidler.addStatement("return " + holder.name);

		clzBuilder.addMethod(getUnitMethodBuidler.build());
	}

	public TypeName getType(String clz) {
		return Reflect.on(TypeName.class).create(clz).get();
	}

	public static String format(String data,Object ... objs) {
		return String.format(data,objs);
	}
}
