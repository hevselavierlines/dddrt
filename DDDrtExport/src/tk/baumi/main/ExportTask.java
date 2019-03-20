package tk.baumi.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.BoundedRangeModel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ExportTask {

	public static void exportBoundedContextsToJava(List<IBoundedContext> boundedContexts, File projectFolder,
			ITextReporter reporter) {
		createAbstracts(projectFolder, reporter);
		for (IBoundedContext boundedContext : boundedContexts) {
//			System.out.println("--" + boundedContext.getContextName() + "--");
			ExportBoundedContextTask boundedContextTask = new ExportBoundedContextTask(boundedContext, projectFolder);
			boundedContextTask.setTextReporter(reporter);
			boundedContextTask.doJavaExport();
			// exportFieldComposites(boundedContext.getContainingComposites());
		}
	}
	
	public static void createAbstracts(File projectFolder, ITextReporter reporter) {
		String packageName = "tk.baumi.ddd";
		File packageFolder = ExportBoundedContextTask.createFolders(packageName, projectFolder);
		
		CompilationUnit compilationUnit = new CompilationUnit(packageName);
		ClassOrInterfaceDeclaration entityClass = compilationUnit.addClass("Entity");
		entityClass.setAbstract(true).setPublic(true);
		
		MethodDeclaration propertiesMethod = entityClass.addMethod("properties", Modifier.PUBLIC, Modifier.ABSTRACT);
		propertiesMethod.setType(Object[].class);
		propertiesMethod.setAbstract(true);
		propertiesMethod.setBody(null);
		
		MethodDeclaration insertMethod = entityClass.addMethod("insert", Modifier.PUBLIC, Modifier.ABSTRACT);
		insertMethod.addParameter(Object[].class, "properties");
		insertMethod.setAbstract(true);
		insertMethod.setBody(null);
		
		StringBuffer prependCode = new StringBuffer();
		prependCode.append("package ");
		prependCode.append(packageName);
		prependCode.append(";\n\r\n\r");
		String code = prependCode.toString() + entityClass.toString();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(packageFolder, "Entity.java"));
			fos.write(code.getBytes("UTF-8"));
			reporter.reportTextln("Wrote class: Entity.java");
		} catch (Exception e) {
			reporter.reportTextln(e.getMessage());
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

	public static String exportBoundedContextToDB(List<IFieldComposite> fieldComposites, List<IDDDRelation> relations) {
		StringBuffer sqlString = new StringBuffer();
		ExportDatabaseTask.createDeleteStatements(sqlString, fieldComposites);

		for (IFieldComposite fieldComposite : fieldComposites) {
			if (fieldComposite.getType() != CompositeType.ValueObject) {
				ExportDatabaseTask.exportFieldCompositeToDBTable(sqlString, fieldComposite);
			}
		}
		for (IDDDRelation relation : relations) {
			if (!relation.relationToValueObject()) {
				ExportDatabaseTask.exportRelationToDB(sqlString, relation);
			}
		}
		sqlString.append("\ncommit;");
		return sqlString.toString();
	}
}
