package tk.baumi.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

import javassist.bytecode.annotation.MemberValue;

public class ExportBoundedContextTask {
	private String contextName;
	private IBoundedContext boundedContext;
	private HashSet<String> packages;
	private File projectFolder;
	private ITextReporter textReporter;

	public ExportBoundedContextTask(IBoundedContext boundedContext, File projectFolder) {
		this.boundedContext = boundedContext;
		this.contextName = boundedContext.getContextName();
		this.packages = new HashSet<String>();
		this.projectFolder = projectFolder;
	}

	public void setTextReporter(ITextReporter textReporter) {
		this.textReporter = textReporter;
	}

	private File createFolders(String packageName, File projectFolder) {
		String[] packagePath = packageName.split("\\.");
		if (!projectFolder.exists()) {
			projectFolder.mkdirs();
		}
		File currentPath = new File(projectFolder.getAbsolutePath());
		for (String partFolder : packagePath) {
			currentPath = new File(currentPath, partFolder);
			currentPath.mkdir();
		}
		return currentPath;
	}

	public void doJavaExport() {
		for (IFieldComposite composite : boundedContext.getContainingComposites()) {
			exportFieldCompositeToJava(composite);
		}
	}

	private void exportFieldCompositeToJava(IFieldComposite field) {
		String packageName = boundedContext.getPackageName(field);
		File packageFolder = createFolders(packageName, projectFolder);

		CompilationUnit compilationUnit = new CompilationUnit(packageName);
		ClassOrInterfaceDeclaration myClass = compilationUnit.addClass(field.getName()).setPublic(false);
		myClass.setPublic(true);
		NodeList<MemberValuePair> tableInfo = new NodeList<MemberValuePair>();
		tableInfo.add(
				new MemberValuePair("tableName",
						new StringLiteralExpr(field.getDatabaseName())));
		myClass.addAnnotation(
				new NormalAnnotationExpr(
						new Name("DDDEntity"), tableInfo));
		List<ExportProperty> exportProperties = new ArrayList<ExportProperty>();
		for (ExportProperty property : field.getProperties()) {
			exportProperties.add(property);
			Modifier visibility = property.getVisibility();
			FieldDeclaration fieldDeclaration = null;
			if (visibility != null) {
				fieldDeclaration = myClass.addField(property.getType(), property.getName(), property.getVisibility());
			} else {
				fieldDeclaration = myClass.addField(property.getType(), property.getName());
			}
			NodeList<MemberValuePair> propertyInfo = new NodeList<MemberValuePair>();
			if(property.isPrimaryProperty()) {
			propertyInfo.add(
					new MemberValuePair("primaryKey",
							new BooleanLiteralExpr(true)));
			}
			propertyInfo.add(
					new MemberValuePair("columnName", 
							new StringLiteralExpr(property.getDatabaseName())));
			propertyInfo.add(
					new MemberValuePair("columnType",
							new StringLiteralExpr(property.getDatabaseType())));
			
			fieldDeclaration.addAnnotation(
					new NormalAnnotationExpr(
							new Name("DDDProperty"), propertyInfo));
		}
		if (field.getType() != CompositeType.ValueObject) {
			myClass.addConstructor(Modifier.PUBLIC);
		}
		ConstructorDeclaration ctor = myClass.addConstructor(Modifier.PUBLIC);
		for (ExportProperty property : exportProperties) {
			ctor.addParameter(property.getType(), "_" + property.getName());
		}

		BlockStmt ctorBody = new BlockStmt();
		for (ExportProperty property : exportProperties) {
			ctorBody.addStatement(property.getName() + " = _" + property.getName() + ";");
		}
		ctor.setBody(ctorBody);

		for (ExportMethod method : field.getMethods()) {
			Modifier visibility = method.getVisibility();
			MethodDeclaration declaration;
			String methodType = method.getType();
			if (visibility != null) {
				declaration = myClass.addMethod(method.getName(), visibility);
			} else {
				declaration = myClass.addMethod(method.getName());
			}
			for (ExportMethod.Parameter parameter : method.getParameters()) {
				String parameterName = parameter.name;
				if (!parameterName.startsWith("_")) {
					parameterName = "_" + parameterName;
				}
				declaration.addParameter(parameter.type, parameterName);
			}
			if (!"void".equals(methodType)) {
				declaration.setType(methodType);
				BlockStmt methodBody = new BlockStmt();

				if ("byte".equals(methodType) || "short".equals(methodType) || "int".equals(methodType)
						|| "float".equals(methodType) || "double".equals(methodType) || "long".equals(methodType)) {
					methodBody.addStatement("return 0;");
				} else if ("char".equals(methodType)) {
					methodBody.addStatement("return \'0\';");
				} else if ("boolean".equals(methodType)) {
					methodBody.addStatement("return false;");
				} else {
					methodBody.addStatement("return null;");
				}
				declaration.setBody(methodBody);
			}
		}

		StringBuffer prependCode = new StringBuffer();
		prependCode.append("package ");
		prependCode.append(packageName);
		prependCode.append(";\n\r\n\r");
		String code = prependCode.toString() + myClass.toString();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(packageFolder, field.getName() + ".java"));
			fos.write(code.getBytes("UTF-8"));
			reportText("Wrote class: " + field.getName() + ".java");
		} catch (Exception e) {
			reportText(e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void reportText(String text) {
		if (textReporter != null) {
			textReporter.reportTextln(text);
		}
	}
}
