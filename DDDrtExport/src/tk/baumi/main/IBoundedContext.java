package tk.baumi.main;

import java.util.List;

public interface IBoundedContext {
	List<IFieldComposite> getContainingComposites();
	
	String getContextName();
	String getPackageName();
	
	String getPackageName(IFieldComposite fieldComposite);
}
