package tk.baumi.main;

import com.github.javaparser.ast.Modifier;

public class ExportProperty {
	private String visibility;
	private String name;
	private String type;
	private String dbName;
	private String dbType;
	private boolean primary;

	public ExportProperty(String visibility, String name, String type) {
		super();
		this.visibility = visibility;
		this.name = name;
		this.type = type;
	}

	public Modifier getVisibility() {
		if ("-".equals(visibility)) {
			return Modifier.PRIVATE;
		} else if ("~".equals(visibility)) {
			return null;
		} else if ("#".equals(visibility)) {
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

	public void setDatabaseInfo(String dbName, String dbType, boolean primary) {
		this.dbName = dbName;
		this.dbType = dbType;
		this.primary = primary;
	}

	public String getDatabaseName() {
		return dbName;
	}

	public String getDatabaseType() {
		return dbType;
	}

	public boolean isPrimaryProperty() {
		return this.primary;
	}
}
