package tk.baumi.main;

import java.util.LinkedList;
import java.util.List;

public class ExportMethod extends ExportProperty {
	private List<Parameter> parameters;
	public ExportMethod(String visibility, String name, String type) {
		super(visibility, name, type);
		this.parameters = new LinkedList<Parameter>();
	}
	
	public void addParameter(String type, String name) {
		parameters.add(new Parameter(type, name));
	}

	public class Parameter {
		private String type;
		private String name;
		
		public Parameter(String type, String name) {
			this.type = type;
			this.name = name;
		}
	}
}
