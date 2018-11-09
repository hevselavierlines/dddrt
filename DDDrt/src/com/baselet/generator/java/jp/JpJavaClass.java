package com.baselet.generator.java.jp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.baselet.generator.java.Field;
import com.baselet.generator.java.JavaClass;
import com.baselet.generator.java.Method;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;

public class JpJavaClass implements JavaClass {

	private final Logger log = Logger.getLogger(JpJavaClass.class);

	private CompilationUnit cu;
	private final List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	private final List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>();
	private ClassOrInterfaceDeclaration clazz;
	private final List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();

	public JpJavaClass(String filename) throws ClassParserException {
		FileInputStream in = null;

		try {
			in = new FileInputStream(filename);
			cu = JavaParser.parse(in);
		} catch (Exception e) {
			throw new ClassParserException("Javaparser library failed to parse " + filename, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				log.error("Exception at Java class parsing", e);
			}
		}
		extractInformation(filename);
	}

	private void extractInformation(String filename) throws ClassParserException {
		List<TypeDeclaration> types = cu.getTypes();
		for (TypeDeclaration type : types) {
			if (type instanceof ClassOrInterfaceDeclaration) {
				clazz = (ClassOrInterfaceDeclaration) type;
			}
			List<BodyDeclaration> members = type.getMembers();
			for (BodyDeclaration member : members) {
				if (member instanceof FieldDeclaration) {
					fields.add((FieldDeclaration) member);
				}
				else if (member instanceof ConstructorDeclaration) {
					constructors.add((ConstructorDeclaration) member);
				}
				else if (member instanceof MethodDeclaration) {
					methods.add((MethodDeclaration) member);
				}
			}
		}
		if (clazz == null) {
			throw new ClassParserException("Could not successfully parse " + filename + ".");
		}
	}

	@Override
	public String getName() {
		return clazz.getName().toString();
	}

	@Override
	public Field[] getFields() {
		Field[] newFields = new Field[fields.size()];
		int i = 0;
		for (FieldDeclaration field : fields) {
			newFields[i] = new JpField(field);
			i++;
		}
		return newFields;
	}

	@Override
	public Method[] getMethods() {
		Method[] newMethods = new Method[methods.size() + constructors.size()];
		int i = 0;
		for (ConstructorDeclaration constructor : constructors) {
			newMethods[i] = new JpConstructor(constructor);
			i++;
		}
		for (MethodDeclaration method : methods) {
			newMethods[i] = new JpMethod(method);
			i++;
		}
		return newMethods;
	}

	@Override
	public ClassRole getRole() {
		if (clazz.isInterface()) {
			return ClassRole.INTERFACE;
		}
		else if ((clazz.getModifiers() & ModifierSet.ABSTRACT) != 0) {
			return ClassRole.ABSTRACT;
		}
		else {
			return ClassRole.CLASS;
		}
	}

	@Override
	public String getPackage() {
		PackageDeclaration packageDecl = cu.getPackage();
		if (packageDecl == null) {
			return "";
		}
		String packageWithExtra = packageDecl.toString().replace("package ", "");
		return packageWithExtra.substring(0, packageWithExtra.lastIndexOf(";"));
	}
}
