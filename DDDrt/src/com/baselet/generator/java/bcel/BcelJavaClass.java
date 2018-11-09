package com.baselet.generator.java.bcel;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.log4j.Logger;

public class BcelJavaClass implements com.baselet.generator.java.JavaClass {

	private static final Logger log = Logger.getLogger(BcelJavaClass.class);

	private JavaClass clazz;
	private String className;

	public BcelJavaClass(String filename) {
		try {
			ClassParser parser = new ClassParser(filename);
			clazz = parser.parse();
		} catch (Exception e) {
			log.error("BCEL library failed to parse " + filename, e);
		}
	}

	@Override
	public String getName() {
		String nameWithPackage = clazz.getClassName();
		className = nameWithPackage.substring(nameWithPackage.lastIndexOf(".") + 1, nameWithPackage.length());
		return className;
	}

	@Override
	public com.baselet.generator.java.Field[] getFields() {
		Field[] fields = clazz.getFields();
		BcelField[] newFields = new BcelField[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			newFields[i] = new BcelField(fields[i]);
		}
		return newFields;
	}

	@Override
	public com.baselet.generator.java.Method[] getMethods() {
		Method[] methods = clazz.getMethods();
		BcelMethod[] newMethods = new BcelMethod[methods.length];
		for (int i = 0; i < methods.length; ++i) {
			newMethods[i] = new BcelMethod(methods[i], className);
		}
		return newMethods;
	}

	@Override
	public ClassRole getRole() {
		if (clazz.isInterface()) {
			return ClassRole.INTERFACE;
		}
		else if (clazz.isAbstract()) {
			return ClassRole.ABSTRACT;
		}
		else {
			return ClassRole.CLASS;
		}
	}

	@Override
	public String getPackage() {
		return clazz.getPackageName();
	}
}
