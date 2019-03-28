package tk.baumi.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ExportTask {

	public static void exportJavaProject(List<IFieldComposite> fieldComposites, File exportZIP,
			ITextReporter reporter) {
//		createAbstracts(projectFolder, reporter);
		ZipOutputStream zos = null;
		ZipFile rawZipFile = null;
		try {
			rawZipFile = new ZipFile(new File("res","dddrtraw.zip"));
			zos = new ZipOutputStream(new FileOutputStream(exportZIP));
			Enumeration<? extends ZipEntry> entries = rawZipFile.entries();
			String sourcePath = null;
		    while(entries.hasMoreElements()){
		        ZipEntry entry = entries.nextElement();
		        System.out.println(entry.getName());
		        if(entry.getName().endsWith("src/")) {
		        	sourcePath = entry.getName();
		        }
		        InputStream stream = rawZipFile.getInputStream(entry);
		        zos.putNextEntry(entry);
		        byte[] data = new byte[4096];
		        int count = 0;
		        while((count = stream.read(data)) > 0) {
		        	zos.write(data, 0, count);
		        }
		        zos.closeEntry();
		    }
		    //Add entries from the DDDrt
			for (IFieldComposite fieldComposite : fieldComposites) {
				String packageName = fieldComposite.getBoundedContext().getPackageName(fieldComposite);
				String relativeFolder = packageName.replaceAll("\\.", "/");
				ExportJavaTask exportTask = new ExportJavaTask(null, fieldComposite);
				StringBuffer zipEntryPath = new StringBuffer();
				zipEntryPath.append(sourcePath).append(relativeFolder).append('/').append(fieldComposite.getName()).append(".java");
				zos.putNextEntry(new ZipEntry(zipEntryPath.toString()));
				exportTask.setTextReporter(reporter);
				exportTask.writeInZip(zos, packageName);
				zos.closeEntry();
				reporter.reportTextln("Added entry: " + zipEntryPath.toString());
			}
			reporter.reportTextln("Finished ZIP: " + exportZIP.getAbsolutePath());
		} catch (Exception ex) {
			reporter.reportTextln("Error writing to Zip: " + ex.getMessage());
		} finally {
			if(zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					reporter.reportTextln("Error closing ZIP: " + e.getMessage());
				}
			}
			if(rawZipFile != null) {
				try {
					rawZipFile.close();
				} catch (IOException e) {
					reporter.reportTextln("Error closing ZIP: " + e.getMessage());
				}
			}
		}

	}
	
//	public static void createAbstracts(File projectFolder, ITextReporter reporter) {
//		String packageName = "tk.baumi.ddd";
//		File packageFolder = ExportJavaTask.createFolders(packageName, projectFolder);
//		
//		CompilationUnit compilationUnit = new CompilationUnit(packageName);
//		createEntityClass(reporter, packageName, packageFolder, compilationUnit);
//		createValueObjectClass(reporter, packageName, packageFolder, compilationUnit);
//	}
//
//	private static void createEntityClass(ITextReporter reporter, String packageName, File packageFolder,
//			CompilationUnit compilationUnit) {
//		ClassOrInterfaceDeclaration entityClass = compilationUnit.addClass("Entity");
//		entityClass.setAbstract(true).setPublic(true);
//		
//		MethodDeclaration propertiesMethod = entityClass.addMethod("properties", Modifier.PUBLIC, Modifier.ABSTRACT);
//		propertiesMethod.setType(Object[].class);
//		propertiesMethod.setAbstract(true);
//		propertiesMethod.setBody(null);
//		
//		MethodDeclaration insertMethod = entityClass.addMethod("insert", Modifier.PUBLIC, Modifier.ABSTRACT);
//		insertMethod.addParameter(Object[].class, "properties");
//		insertMethod.setAbstract(true);
//		insertMethod.setBody(null);
//		
//		StringBuffer prependCode = new StringBuffer();
//		prependCode.append("package ");
//		prependCode.append(packageName);
//		prependCode.append(";\n\r\n\r");
//		String code = prependCode.toString() + entityClass.toString();
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(new File(packageFolder, "Entity.java"));
//			fos.write(code.getBytes("UTF-8"));
//			reporter.reportTextln("Wrote class: Entity.java");
//		} catch (Exception e) {
//			reporter.reportTextln(e.getMessage());
//		} finally {
//			if (fos != null) {
//				try {
//					fos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	
//	private static void createValueObjectClass(ITextReporter reporter, String packageName, File packageFolder,
//			CompilationUnit compilationUnit) {
//		ClassOrInterfaceDeclaration valueObjectClass = compilationUnit.addClass("ValueObject");
//		valueObjectClass.setAbstract(true).setPublic(true);
//		
//		MethodDeclaration propertiesMethod = valueObjectClass.addMethod("properties", Modifier.PUBLIC, Modifier.ABSTRACT);
//		propertiesMethod.setType(Object[].class);
//		propertiesMethod.setAbstract(true);
//		propertiesMethod.setBody(null);
//		
//		MethodDeclaration insertMethod = valueObjectClass.addMethod("insert", Modifier.PUBLIC, Modifier.ABSTRACT);
//		insertMethod.addParameter(Object[].class, "properties");
//		insertMethod.setAbstract(true);
//		insertMethod.setBody(null);
//		
//		/*public JSONArray serialize() {
//			JSONArray ret = new JSONArray();
//			Object[] properties = properties();
//			for(Object property : properties) {
//				if(!ValueObject.class.isAssignableFrom(property.getClass())) {
//					ret.put(property);
//				}
//			}
//			return ret;
//		}*/
//		MethodDeclaration serializeMethod = valueObjectClass.addMethod("serialize", Modifier.PUBLIC);
//		serializeMethod.setType("org.json.JSONArray");
//		BlockStmt serializeBlock = new BlockStmt();
//		serializeBlock.addStatement("org.json.JSONArray ret = new org.json.JSONArray();");
//		serializeBlock.addStatement("Object[] properties = properties();");
//		StringBuffer serializeFor = new StringBuffer();
//		serializeFor.append("for(Object property : properties) {\n");
//		serializeFor.append("if(!ValueObject.class.isAssignableFrom(property.getClass())) {\n");
//		serializeFor.append("ret.put(property);\n");
//		serializeFor.append("}\n");
//		serializeFor.append("}\n");
//		serializeBlock.addStatement(serializeFor.toString());
//		serializeBlock.addStatement("return ret;");
//		serializeMethod.setBody(serializeBlock);
//		
//		/*public void deserialize(JSONArray json) {
//			Object[] elems = new Object[json.length()];
//			for(int i = 0; i < elems.length; i++) {
//				Object elem = json.get(i);
//				if(!ValueObject.class.isAssignableFrom(elem.getClass())) {
//					elems[i] = elem;
//				}
//			}
//			insert(elems);
//		}*/
//		MethodDeclaration deserializeMethod = valueObjectClass.addMethod("deserialize", Modifier.PUBLIC);
//		deserializeMethod.addParameter("org.json.JSONArray", "json");
//		BlockStmt deserializeBlock = new BlockStmt();
//		deserializeBlock.addStatement("Object[] elems = new Object[json.length()];");
//		StringBuffer deserializeFor = new StringBuffer();
//		deserializeFor.append("for(int i = 0; i < elems.length; i++) {\n");
//		deserializeFor.append("Object elem = json.get(i);\n");
//		deserializeFor.append("if(!ValueObject.class.isAssignableFrom(elem.getClass())) {\n");
//		deserializeFor.append("elems[i] = elem;\n");
//		deserializeFor.append("}\n");
//		deserializeFor.append("}\n");
//		deserializeBlock.addStatement(deserializeFor.toString());
//		deserializeBlock.addStatement("insert(elems);");
//		deserializeMethod.setBody(deserializeBlock);
//		
//		StringBuffer prependCode = new StringBuffer();
//		prependCode.append("package ");
//		prependCode.append(packageName);
//		prependCode.append(";\n\r\n\r");
//		String code = prependCode.toString() + valueObjectClass.toString();
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(new File(packageFolder, "ValueObject.java"));
//			fos.write(code.getBytes("UTF-8"));
//			reporter.reportTextln("Wrote class: ValueObject.java");
//		} catch (Exception e) {
//			reporter.reportTextln(e.getMessage());
//		} finally {
//			if (fos != null) {
//				try {
//					fos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	public static String exportBoundedContextToDB(List<IFieldComposite> fieldComposites, List<IDDDRelation> relations) {
		StringBuffer sqlString = new StringBuffer();
		ExportDatabaseTask.createDeleteStatements(sqlString, fieldComposites);

		for (IFieldComposite fieldComposite : fieldComposites) {
			if (fieldComposite.requireDatabaseInformation()) {
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
