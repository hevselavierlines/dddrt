package tk.baumi.main;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import tk.baumi.test.DDDEntity;
import tk.baumi.test.DDDProperty;
import tk.baumi.test.Entity1;
import tk.baumi.test.RepositoryTest;

public class MainClass {

	public static void main(String[] args) {

		
//		createClass();
		
		RepositoryTest repoTest = 
				new RepositoryTest("jdbc:oracle:thin:@localhost:1521:xe", 
						"afaci", "afaci");
		Entity1 entU = new Entity1("MARS", "TEST2");
		repoTest.update(entU);
		List<Entity1> entities = repoTest.selectAll(Entity1.class);
		for(Entity1 entity : entities) {
			System.out.println(entity.toString());
		}
		
//		Entity1 ent = new Entity1(UUID.randomUUID().toString(), "\\chapter{Related Work}\r\n" + 
//				"\\section{Visual Studio Entity Framework}\r\n" + 
//				"\\begin{figure} \r\n" + 
//				"	\\includegraphics[width=1.0\\columnwidth]{images/vsef_vet_model}\r\n" + 
//				"	\\caption{\\emph{Visual Studio Entity Framework} example}~\\label{fig:vsefexample}\r\n" + 
//				"\\end{figure}\r\n" + 
//				"Visual Studio itself is a development software provided by Microsoft. Code can be written in Visual Basic, C++, C\\# and even more programming languages but it is commonly used for C\\# added with different technologies and frameworks helping the developers. One of these frameworks handles the interaction between the database and the model of the software which is called a repository. In this case, it is called the \\emph{Entity Framework}.\r\n" + 
//				"\r\n" + 
//				"The creation of the model including the database and the code works with three different approaches. The decision can depend on the state of the project \r\n" + 
//				"\\begin{description}\r\n" + 
//				"    \\item[Database First] is used for projects with an existing and working database including the model (tables, views, et al.) meaning that the project does not have to start from scratch~\\cite{entityframeworkjulialerman}. With an import dialogue the user has to select these objects that should be part of the model. With the import the framework automatically creates the UML representation allowing to customise the code that should be created out of it.\r\n" + 
//				"    \\item[Code First] means that instead of the database the code is created firstly. The database tables are going to be created from this code including the UML representation. The only drawback using this approach is that all changes in the code have to be pushed again to the database.\r\n" + 
//				"    \\item[Model First] is a different approach via the UML diagram designer. An example of this designer with the veterinarian clinic for the appointment can be seen at figure~\\ref{fig:vsefexample}. The user creates all the classes in this designer including the relations between the entities. The usual properties are listed in the properties pane and the foreign key properties are listed in the \\emph{Navigation Properties} panel. The code representation of the model will always be created if a change occurs but this is not the case for the database. Having finished the design, the \\emph{Generate Database from Model} entry has to be selected from the context menu starting the process for creating the database out of it, however, it actually just creates the SQL script that the user has to execute.\r\n" + 
//				"\\end{description}\r\n" + 
//				"With all these three approaches the \\emph{Model First} approach is used to create the conceptual model by using \\emph{Domain-Driven} patters and let the database and code created out of it~\\cite{entityframeworkjulialerman}. Additionally, the creating of the model can be supported via the database input using the \\emph{Database First} approach.\r\n" + 
//				"\r\n" + 
//				"Beside the task of the creating the model, the entity framework brings more advantages for the developers.\r\n" + 
//				"Firstly, having created a model from the database or having designed the model from scratch in the designer, all the code representing the model is automatically generated. Moreover, all changes in the designer that have been made afterwards are also transferred to this model keeping it up-to-date. Secondly, the connection from the program to the database is handled by the framework decreasing the amount of code that has to be written for the database requests~\\cite{entityframeworkjulialerman}.\r\n" + 
//				"\r\n" + 
//				"The designer itself is both for checking the current state of the data model as it has been imported form the database or the code and refactoring this model. Most of the refactoring operations start from the context menu and from the toolbox. Adding a new entity and relations are good examples for typical refactoring operations. Additionally, the creation operations are mostly in dialogues determining the name of an entity and the key property (primary key). This is also true for the associations as they have to be created from an entity to another entity. The dialogue here needs both ends of the relation with the multiplicity being 0, 1 or * and the names of the properties for the access called the navigation properties.\r\n" + 
//				"\r\n" + 
//				"\\section{Eclipse Modelling Framework}\r\n" + 
//				"Eclipse, being an free IDE mostly for Java, is using a lot of tools and frameworks to support the developers. A specific framework for model creation called the \\emph{Eclipse Modelling Framework}.\r\n" + 
//				"This framework is preliminary made for the modelling first approach. Firstly, the model is designed. Secondly, other tasks are considered such as the user interface. The \\emph{Eclipse Modelling Framework} should help with designing this model and providing code generation functionality~\\cite{emfbillmore}. Moreover, it is certainly a unification of UML, XML and Java~\\cite{emfsteinberg}. XML here can be used for the description of a model and also for storing it as a file. This unification also means that the definition of the model has to be in one of these formats and the modelling framework will create the other two formats out of it~\\cite{emfsteinberg}. With DDD a good example is to create the UML diagram first and the EMF creates the Java implementation as well as the XML representation out of it.\r\n" + 
//				"\r\n" + 
//				"The description of the model is defined in the \\emph{ECore} that is also a model itself called a meta-model. The general elements of a simple class, as in the example in figure~\\ref{fig:emfexample}, starting with the\r\n" + 
//				"\\begin{description}\r\n" + 
//				"    \\item[EClass] to begin the EMF representation of a \\emph{class} with at least a name. Attributes and references are optional but in most cases it should have a couple of attributes, whereby, there might be no references or very few of them. The attributes are described as an\r\n" + 
//				"    \\item[EAttribute] with a name and a type but mostly primitive types such as int, char and long or object types extending from\r\n" + 
//				"    \\item[EDateType] for this type representation. These types are a bit different to default Java data types because all of them use an \\emph{E} prefix. Good examples are EString, EInt and EDate. However, in the case of referencing another class in a diagram the\r\n" + 
//				"    \\item[EReference] comes into place. It replaces the \\emph{EAttribute} as a definition of the relationship between two classes by associating the type of the destination class. The main attributes of an reference are also the name with the target type, which is here the type of another class in the same diagram. Additionally, it requires a boolean flag indicating the representation of a containment~\\cite{emfsteinberg}.\r\n" + 
//				"\\end{description}\r\n" + 
//				" \r\n" + 
//				" These \\emph{ECore} model can be created in the editor as a tree structure or via the XML file but for DDD the UML designer is the main area of interest. These designer allows the user to create new classes, adding attributes and handle the association between the elements. All these operations can be done with the toolbox. After the selection of an element, the user has to select the desired location for a new class or the class for new attributes or associations.\r\n" + 
//				" \r\n" + 
//				" The next step is to build the Java model out of the designed UML model. This can be done via the context menu entry \\emph{Generate > Model Code} with some additional functionalities to create an editor based on this model~\\cite{emfkellysteven} with the context menu entry \\emph{Generate > Edit Code}. \r\n" + 
//				"\\begin{figure}\r\n" + 
//				"	\\includegraphics[width=1.0\\columnwidth]{images/testEMF1}\r\n" + 
//				"	\\caption{\\emph{Eclipse Modelling Framework} example}~\\label{fig:emfexample}\r\n" + 
//				"\\end{figure}\r\n" + 
//				"\r\n" + 
//				"\\section{UMLet}\r\n" + 
//				"Firstly, UML is the de facto standard for graphical modelling~\\cite{rumbaugh2005unified}. Secondly, most products are part of a framework or an IDE working only inside this development area or programming language.\r\n" + 
//				"\\emph{UMLet} is an open source, standalone software for designing UML diagrams. It was written in Java also with the functionality of gathering information from Java source or built class files using external libraries such as JaPa (Java Parser). This is kind of an code first approach, whereby, the code will not be modified. Although this parts are limited to Java, it should bring graphical help without being restricted to any development area~\\cite{Auer:2003:FUM:942796.943259}. It is so called a flyweight tool for model creation~\\cite{Auer:2003:FUM:942796.943259}.\r\n" + 
//				"It does already contain the elements to create a graphical representation of the model by easily drawing them with a simple mark-up and without input dialogues~\\cite{Auer:2003:FUM:942796.943259}.\r\n" + 
//				"\r\n" + 
//				"The GUI is using several containers and controls of the Java Swing library. An example can be seen at figure~\\ref{fig:umletexample}.\r\n" + 
//				"The main drawing area can open up multiple files with the different tab-folders. The tool-bar on the right side also uses almost the same functionality as the main drawing area. Furthermore, it allows the user to drag and drop an object from the tool-bar into the drawing area. A copy will be created in this process.\r\n" + 
//				"\r\n" + 
//				"\\begin{figure} \r\n" + 
//				"	\\includegraphics[width=1.0\\columnwidth]{images/umletexamplevet}\r\n" + 
//				"	\\caption{\\emph{UMLet} example}~\\label{fig:umletexample}\r\n" + 
//				"\\end{figure}\r\n" + 
//				"\\emph{UML} itself consists of multiple types of diagrams. The key aspect is the class diagram. A class diagram is considered as the default in UML\\cite{fowler2004uml}. In all object-oriented Programming languages class diagrams are very helpful in designing the properties and methods by having a quick overview. With \\emph{UMLet}, it is possible to create such diagrams by defining the properties and methods in the properties input-field on the right bottom corner. Although the specific rules about the structure of properties with the\r\n" + 
//				"\\begin{itemize}\r\n" + 
//				"	\\item visibility (private, package, protected, public),\r\n" + 
//				"	\\item name and the\r\n" + 
//				"	\\item type (String, int, et al.)\r\n" + 
//				"\\end{itemize}	\r\n" + 
//				"as it is described in more details in the section~\\ref{sec:classdiagram}. However, in \\emph{UMLet} the user is not bound to this structure and this is also goal of the software itself creating a lightweight version for UML diagrams~\\cite{Auer:2003:FUM:942796.943259}. \r\n" + 
//				"\r\n" + 
//				"Especially relations can be drawn from any type of diagram element to another. They will just keep the same relative position of the bounding polygon, the sticky polygon, after the element has been moved to another position. Moreover, types can also be changed in an easier way compared to different platform dependent modelling tools by the properties text-input~\\cite{Auer:2003:FUM:942796.943259}. Moreover, all the other defined elements, such as the multiplicity, are reused.\r\n" + 
//				"\r\n" + 
//				"This is also true for whole classes and all other elements because a copy of them has basically the same properties and size. Nonetheless, it is a completely new element and changes will not affect the original one.\r\n" + 
//				"In a nutshell, this means that almost all of the user defined definitions and customisation happen in this text form supporting default keyboard shortcuts and avoiding the usage of multiple dialogues~\\cite{Auer:2003:FUM:942796.943259}.\r\n" + 
//				"\r\n" + 
//				"Lastly, it is also useful to share this diagrams across the team or use it in presentations. Communication is also an important aspect in UML~\\cite{Auer:2003:FUM:942796.943259}. The user of this diagram might require this in a specific format such as a high or low resolution raster graphic (JPEG, PNG) or a vector graphic (SVG, PDF). External open-source libraries are used, such as Batik for SVG files, making it extendable for more formats~\\cite{Auer:2003:FUM:942796.943259}.");
//		repoTest.insert(ent);
		repoTest.disconnect();
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
