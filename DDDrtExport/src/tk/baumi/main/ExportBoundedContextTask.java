package tk.baumi.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

public class ExportBoundedContextTask {
	private String contextName;
	private String packageName;
	private File contextFolder;
	private IBoundedContext boundedContext;
	
	public ExportBoundedContextTask(IBoundedContext boundedContext) {
		this.boundedContext = boundedContext;
		this.contextName = boundedContext.getContextName();
		this.packageName = boundedContext.getPackageName();
	}
	
	public ExportBoundedContextTask createFolders(File projectFolder) {
		String[] packagePath = this.packageName.split("\\.");
		if(!projectFolder.exists()) {
			projectFolder.mkdirs();
		}
		File currentPath = new File(projectFolder.getAbsolutePath());
		for(String partFolder : packagePath) {
			currentPath = new File(currentPath, partFolder);
			currentPath.mkdir();
		}
		this.contextFolder = currentPath;
		return this;
	}
	
	public void doExport() {
		CompilationUnit compilationUnit = new CompilationUnit(packageName);
		for(IFieldComposite composite : boundedContext.getContainingComposites()) {
			exportFieldComposite(compilationUnit, composite);
		}
	}
	
	private void exportFieldComposite(CompilationUnit compilationUnit, IFieldComposite field) {
		ClassOrInterfaceDeclaration myClass = compilationUnit
		        .addClass(field.getName())
		        .setPublic(false);
		List<ExportProperty> exportProperties = new ArrayList<ExportProperty>();
		for(ExportProperty property : field.getProperties()) {
			exportProperties.add(property);
			Modifier visibility = property.getVisibility();
			if(visibility != null) {
				myClass.addField(property.getType(), property.getName(), property.getVisibility());
			} else {
				myClass.addField(property.getType(), property.getName());
			}
		}
		
		if(field.getType() != CompositeType.ValueObject) {
			myClass.addConstructor(Modifier.PUBLIC);
		}
		ConstructorDeclaration ctor = myClass.addConstructor(Modifier.PUBLIC);
		for(ExportProperty property : exportProperties) {
			ctor.addParameter(property.getType(), "_" + property.getName());
		}
		
		BlockStmt ctorBody = new BlockStmt();
		for(ExportProperty property : exportProperties) {
			ctorBody.addStatement(property.getName() + " = _" + property.getName() + ";");
		}
		ctor.setBody(ctorBody);
		
		for(ExportMethod method : field.getMethods()) {
			Modifier visibility = method.getVisibility();
			MethodDeclaration declaration;
			String methodType = method.getType();
			if(visibility != null) {
				declaration = myClass.addMethod(method.getName(), visibility);
			} else {
				declaration = myClass.addMethod(method.getName());
			}
			for(ExportMethod.Parameter parameter : method.getParameters()) {
				String parameterName = parameter.name;
				if(!parameterName.startsWith("_")) {
					parameterName = "_" + parameterName;
				}
				declaration.addParameter(parameter.type, parameterName);
			}
			if(!"void".equals(methodType)) {
				declaration.setType(methodType);
				BlockStmt methodBody = new BlockStmt();
				
				if("byte".equals(methodType) || "short".equals(methodType) || 
						"int".equals(methodType) || "float".equals(methodType) ||
						"double".equals(methodType) ||"long".equals(methodType)) {
					methodBody.addStatement("return 0;");
				} else if("char".equals(methodType)) {
					methodBody.addStatement("return \'0\';");
				} else if("boolean".equals(methodType)) {
					methodBody.addStatement("return false;");
				} else {
					methodBody.addStatement("return null;");
				}
				declaration.setBody(methodBody);
			}
		}
		
		
		StringBuffer prependCode = new StringBuffer();
		prependCode.append("package ");
		prependCode.append(this.packageName);
		prependCode.append(";\n\r\n\r");
		String code = prependCode.toString() + myClass.toString();
		FileOutputStream fos = null; 
		try {
			fos = new FileOutputStream(new File(contextFolder, field.getName()+ ".java"));
			fos.write(code.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
