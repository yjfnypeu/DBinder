package com.lzh.dbinderproc;

import com.google.auto.service.AutoService;
import com.lzh.dbinder.annotation.Bind;
import com.lzh.dbinderproc.util.FileLog;
import com.lzh.dbinderproc.util.UtilMgr;
import com.lzh.dbinderproc.util.ValidUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;


/**
 * DBinder compiler time processor
 */
@AutoService(Processor.class)
public class DBinderProcessor extends AbstractProcessor {
	Elements elementUtils = null;
	Filer filer = null;
	Messager messager = null;
	Types typeUtils = null;
	
	Map<String, BindContainer> maps = new HashMap<String, BindContainer>();

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new HashSet<String>();
		types.add(Bind.class.getCanonicalName());
		return types;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		elementUtils = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
		typeUtils = processingEnv.getTypeUtils();
		UtilMgr mgr = UtilMgr.getMgr();
		mgr.setElementUtils(elementUtils);
		mgr.setFiler(filer);
		mgr.setMessager(messager);
		mgr.setTypeUtils(typeUtils);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		maps.clear();
		try {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Bind.class);
			if (elements.size() != 0) {
				FileLog.reload();
			}
			FileLog.print("process starting");
			for (Element ele : elements) {
				FileLog.print(ele.getSimpleName().toString());
				if (ele.getKind() != ElementKind.FIELD) {
					error("该注解只能为成员变量所使用", ele);
					return true;
				}

				VariableElement varEle = (VariableElement) ele;
				// cast field element to class element
				TypeElement element = (TypeElement) varEle.getEnclosingElement();
				String className = element.getQualifiedName().toString();
				BindContainer container = maps.get(className);
				// check and filter unreachable classes
				if (container == null ) {
					if (!ValidUtil.isClassElementValid(element)) {
						throw new ProcException(element, "class %s must be modified by public/protected but non-final/abstract", element.getSimpleName());
					}
					FileLog.print("put class name %s into maps", className);
					container = new BindContainer(element);
					maps.put(className, container);
				}
			}
			
			Set<String> keySet = maps.keySet();
			for (String key:keySet) {
				BindContainer bindContainer = maps.get(key);
				bindContainer.generateCode();
			}
			
			FileLog.print("process end");
		} catch (ProcException pe) {
			FileLog.printException(pe);
			error(pe.getMessage(), pe.getElement());
		} catch (Exception e) {
			FileLog.printException(e);
			error(e.getMessage());
		} catch (Error e) {
			FileLog.printException(e);
			error(e.getMessage());
		}
		return true;
	}
	
	void error(String errorMsg,Element e) {
		if (e == null) {
			messager.printMessage(Kind.ERROR, errorMsg);
		} else {
			messager.printMessage(Kind.ERROR, errorMsg, e);
		}
	}
	
	void error(String errorMsg) {
		error(errorMsg, null);
	}
	
}
