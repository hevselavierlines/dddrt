package tk.baumi.main;

import com.github.javaparser.ast.Modifier;

public class ExportProperty {
	private String visibility;
	private String name;
	private String type;
	public ExportProperty(String visibility, String name, String type) {
		super();
		this.visibility = visibility;
		this.name = name;
		this.type = type;
	}
	public Modifier getVisibility() {
		if("-".equals(visibility)) {
			return Modifier.PRIVATE;
		} else if("~".equals(visibility)) {
			return null;
		} else if("#".equals(visibility)) {
			return Modifier.PROTECTED;
		} else {
			return Modifier.PUBLIC;
		}
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
}
