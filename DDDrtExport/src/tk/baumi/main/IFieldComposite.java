package tk.baumi.main;

import java.util.List;
import java.util.Map;

public interface IFieldComposite {
	String getName();
	List<ExportProperty> getProperties();
	List<ExportMethod> getMethods();
	CompositeType getType();
}
