package tk.baumi.main;

import org.w3c.dom.svg.SVGDocument;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class MainClass {

	public static void main(String[] args) {
		CompilationUnit compilationUnit = new CompilationUnit();
		ClassOrInterfaceDeclaration myClass = compilationUnit
		        .addClass("MyClass")
		        .setPublic(true);
		myClass.addField(int.class, "A_CONSTANT", Modifier.PUBLIC, Modifier.STATIC);
		myClass.addField(String.class, "name", Modifier.PRIVATE);
		String code = myClass.toString();
		System.out.println(code);
	}

}
