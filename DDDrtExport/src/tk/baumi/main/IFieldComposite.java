package tk.baumi.main;

import java.util.List;
import java.util.Map;

public interface IFieldComposite extends IValidateable {
	String getName();

	String getDatabaseName();

	List<ExportProperty> getProperties();

	List<ExportMethod> getMethods();

	CompositeType getType();
	
	boolean showProperties();
	
	boolean requireDatabaseInformation();
	
	IBoundedContext getBoundedContext();
}
