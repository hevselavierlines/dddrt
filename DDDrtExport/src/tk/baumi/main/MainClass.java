package tk.baumi.main;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import tk.baumi.ddd.DDDEntity;
import tk.baumi.ddd.DDDProperty;
import tk.baumi.ddd.Repository;
import tk.baumi.test.Aggregate1;
import tk.baumi.test.Entity1;
import tk.baumi.test.Entity2;
import tk.baumi.test.VO1;

public class MainClass {

	public static void main(String[] args) {
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid.toString());
	}

	private static void createClass() {
		CompilationUnit compilationUnit = new CompilationUnit();
		ClassOrInterfaceDeclaration myClass = compilationUnit.addClass("MyClass").setPublic(true);
		myClass.addField(int.class, "A_CONSTANT", Modifier.PUBLIC, Modifier.STATIC);
		myClass.addField(String.class, "name", Modifier.PRIVATE);

		MethodDeclaration method = myClass.addMethod("addSomething", Modifier.PUBLIC);
		method.setType("String");
		method.addParameter(String.class, "connectionString");
		method.addParameter(String.class, "username");
		method.addParameter(String.class, "password");
		BlockStmt blockStmt = new BlockStmt();
		blockStmt.addStatement("Class.forName(\"oracle.jdbc.driver.OracleDriver\");");
		blockStmt.addStatement("connection = DriverManager.getConnection(connectionString, username, password);");
		// blockStmt.addStatement("return null;");
		method.setBody(blockStmt);
		blockStmt.addStatement("name = username;");
		String code = myClass.toString();
		System.out.println(code);
	}

	public void exportClass(IFieldComposite fieldComposite) {
		List<ExportProperty> exportProperies = fieldComposite.getProperties();
	}

	public void exportModelToJavaCode() {

	}

}
