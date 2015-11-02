package com.lzh.dbinderproc.util;

import com.lzh.dbinder.annotation.Bind;
import com.lzh.dbinder.unit.IBindUnit;
import com.lzh.dbinderproc.ProcException;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


public class ValidUtil {
	
	public static boolean isElementValid(Element element) throws Exception {
		ElementKind kind = element.getKind();
		switch (kind) {
		case CLASS:
			return isClassElementValid(element);
		case FIELD:
			return isFieldElementValid(element);
		case METHOD:
			return isMethodElementValid(element);
		case CONSTRUCTOR:
			return isConstrElementValid(element);
		default:
			break;
		}
		return false;
	}
	
	/**
	 * 构造方法需要为无参数,并为public修饰。无final
	 * @param element
	 * @return
	 */
	public static boolean isConstrElementValid(Element element) {
		ElementKind kind = element.getKind();
		if (kind != ElementKind.CONSTRUCTOR) {
			return false;
		}
		
		ExecutableElement executableElement = (ExecutableElement) element;
		if (executableElement.getParameters().size() != 0) {
			return false;
		}
		Set<Modifier> modifiers = executableElement.getModifiers();
		
		if (modifiers.contains(Modifier.PUBLIC) 
				&& !modifiers.contains(Modifier.FINAL)) {
			return true;
		}
		return false;
	}

	/**
	 * 类名必须有public/protect修饰。不能有final/abstract修饰符
	 * @throws Exception 
	 */
	public static boolean isClassElementValid(Element classElement) throws Exception {
		ElementKind kind = classElement.getKind();
		if (kind != ElementKind.CLASS) {
			return false;
		}
		Set<Modifier> modifiers = classElement.getModifiers();
		FileLog.print("class %s modifieres", modifiers);
		if ((modifiers.contains(Modifier.PUBLIC)
				|| modifiers.contains(Modifier.PROTECTED))
				&& !modifiers.contains(Modifier.FINAL)
				&& !modifiers.contains(Modifier.ABSTRACT)
				) {
			return true;
		}
		return false;
	}
	
	/**
	 * 此元素是否有使用Bind注解
	 * @param element
	 * @return
	 * @throws Exception 
	 */
	public static boolean hasBindAnno (Element element) throws Exception {
		return element.getAnnotation(Bind.class) != null;
	}
	
	/**
	 * 检测此元素的超类是否有使用Bind注解
	 * @param classElement
	 * @return
	 * @throws Exception 
	 */
	public static boolean hasSuperBindAnno(TypeElement classElement) throws Exception {
		if (classElement == null) {
			return false;
		}
		
		List<? extends Element> enclosedElements = classElement.getEnclosedElements();
		for (Element ele : enclosedElements) {
			if (ele.getKind() == ElementKind.FIELD && hasBindAnno(ele)) {
				return true;
			}
		}
		
		TypeMirror superclass = classElement.getSuperclass();
		if (superclass.getKind() == TypeKind.NONE) {
			return false;
		}
		Elements utils = UtilMgr.getMgr().getElementUtils();
		TypeElement superElement = utils.getTypeElement(superclass.toString());
		
		return hasSuperBindAnno(superElement);
	}
	
	/**
	 * 字段修饰符不能含有final/static
	 */
	public static boolean isFieldElementValid(Element variableElement) {
		ElementKind kind = variableElement.getKind();
		if (kind != ElementKind.FIELD) {
			return false;
		}
		Set<Modifier> modifiers = variableElement.getModifiers();
		if (!modifiers.contains(Modifier.FINAL)
				&& !modifiers.contains(Modifier.STATIC)
				) {
			return true;
		}
		return false;
	}
	
	/**
	 * 方法修饰符不能含有final/native/static/abstract
	 */
	public static boolean isMethodElementValid(Element executableElement) {
		ElementKind kind = executableElement.getKind();
		if (kind != ElementKind.METHOD) {
			return false;
		}
		Set<Modifier> modifiers = executableElement.getModifiers();
		if (!modifiers.contains(Modifier.FINAL)
				&& !modifiers.contains(Modifier.NATIVE)
				&& !modifiers.contains(Modifier.STATIC)
				&& !modifiers.contains(Modifier.ABSTRACT)
				) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断方法名是否有效：含有is/set/get前缀
	 * @param methodName
	 * @return
	 */
	public static boolean isMethodNameValid(String methodName) {
		if(StringUtils.isEmpty(methodName)) {
			return false;
		}
		if (methodName.startsWith("is")
				|| methodName.startsWith("set")
				|| methodName.startsWith("get")
				) {
			return true;
		}
		return false;
	}
	
	/**
	 * 对于字段资源ele进行Bind注解检查。是否有效,需要view与clz均被指定且clz不能为IBindUnit.class
	 * 需要字段的修饰符不能为final/static修饰
	 * @param ele 
	 * @return 该字段是否被Bind注解
	 * @throws Exception 
	 */
	public static boolean checkBindAnnotationValid(VariableElement ele) throws Exception {
		Bind bind = ele.getAnnotation(Bind.class);
		if (bind == null) {
			return false;
		}
		// check out if field element not valid
		if (!ValidUtil.isFieldElementValid(ele)) {
			throw new ProcException(ele, "The bind %s on field %s is not valid.", bind,ele.getSimpleName());
		}
		
		// check out if bind of element valid
		String className = null;
		className = getClzNameByBind(bind);
		
		if (bind.view() == -1) {
			throw new ProcException(ele,
					"Bind of %s on %s must have view id", 
					bind,ele.getSimpleName());
		}
		
		if (className.equals(IBindUnit.class.getCanonicalName())
				) {
			throw new ProcException(ele,
					"Bind of %s on %s must have clz,and should not be IBindUnit.class", 
					bind,ele);
		}
		
		// get declaration type of field
		TypeMirror fieldType = ele.asType();
		fieldType = boxed(fieldType);
		
		Types typeUtils = UtilMgr.getMgr().getTypeUtils();
		Elements elementUtils = UtilMgr.getMgr().getElementUtils();
		
		// check out if declaration type is valid
		TypeElement unitElement = elementUtils.getTypeElement(className);
		FileLog.print("start check out declaration type!");
		
		ExecutableElement unBindElement = (ExecutableElement) getElementByName(
				"unBindData", ElementKind.METHOD, unitElement);
		TypeMirror bindType = unBindElement.getReturnType();
		bindType = boxed(bindType);
		
		if(!typeUtils.isSubtype(fieldType, bindType)
				&& !typeUtils.isSameType(fieldType, bindType)
				&& !typeUtils.isAssignable(fieldType, bindType)
				) {
			throw new ProcException(ele, "The field type on %s is %s but requires %s", ele,fieldType,bindType);
		}
		return true;
		
	}

	public static String getClzNameByBind(Bind bind) {
		String className;
		try {
			// 该类已编译存在时
			className = bind.clz().getCanonicalName();
		} catch (MirroredTypeException mte) {
			// 该类未编译过，需要从mte中获取类名
			DeclaredType declaredType = (DeclaredType) mte.getTypeMirror();
			TypeElement classTypeElement = (TypeElement) declaredType.asElement();
			className = classTypeElement.getQualifiedName().toString();
		}
		return className;
	}
	
	/**
	 * 
	 * @param src
	 * @param target
	 * @return
	 */
	public static boolean isSameType(TypeMirror src,TypeMirror target) {
		src = boxed(src);
		target = boxed(target);
		Types typeUtils = UtilMgr.getMgr().getTypeUtils();
		if(!typeUtils.isSubtype(src, target)
				&& !typeUtils.isSameType(src, target)
				&& !typeUtils.isAssignable(src, target)
				) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 是否为基本类型
	 * @param mirror
	 * @return
	 */
	public static boolean isBaseType(TypeMirror mirror) {
		switch (mirror.getKind()) {
		case INT:
		case BOOLEAN:
		case BYTE:
		case DOUBLE:
		case CHAR:
		case FLOAT:
			return true;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * 对mirror代表的数据类型进行判断。是否为基本数据类型。是基本数据类型则进行装箱处理
	 * @param mirror
	 * @return
	 */
	public static TypeMirror boxed(TypeMirror mirror) {
		Types typeUtils = UtilMgr.getMgr().getTypeUtils();
		if (isBaseType(mirror)) {
			PrimitiveType primitiveType = typeUtils.getPrimitiveType(mirror.getKind());
			return UtilMgr.getMgr().getTypeUtils().boxedClass(primitiveType).asType();
		}
		return mirror;
	}
	
	public static Element getElementByName(String name,ElementKind kind,Element ele) {
		List<? extends Element> elements = ele.getEnclosedElements();
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			if (element.getKind() == kind 
					&& element.getSimpleName().toString().equals(name)) {
				return elements.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 检查对于get方法method是否匹配field成员变量。需要无参、返回值类型与field一致
	 * @param method
	 * @param field
	 * @return
	 * @throws ProcException 
	 */
	public static boolean checkGetMethodValid(ExecutableElement method,VariableElement field) throws ProcException {
		if (method.getParameters().size() > 0) {
			throw new ProcException(field, "The field %s get method %s should has an empty params"
					, field.getSimpleName(),method);
		}
		
		if (!isSameType(field.asType(), method.getReturnType())) {
			throw new ProcException(field, "The method return type %s is not adjusted to %s on %s"
					, method.getReturnType(),field.asType(),field.getSimpleName());
		}
		return true;
	}
	
	/**
	 * 检查对于set方法method是否匹配field成员变量。需要无返回值，参数唯一且与field相匹配
	 * @param method
	 * @param field
	 * @return
	 * @throws ProcException 
	 */
	public static boolean checkSetMethodValid(ExecutableElement method,VariableElement field) throws ProcException {
		List<? extends VariableElement> parameters = method.getParameters();
		if (parameters.size() != 1 || 
				!isSameType(parameters.get(0).asType(), field.asType())) {
			throw new ProcException(field, "The method %s must have a single params and should be adjusted to field type:%s",
					method.getSimpleName(),field.asType());
		}
		return true;
	}
	
	
	
}
