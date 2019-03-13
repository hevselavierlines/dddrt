package tk.baumi.main;

import java.io.File;
import java.util.List;

import javax.swing.BoundedRangeModel;

public class ExportTask {
	
	public static void exportBoundedContexts(List<IBoundedContext> boundedContexts) {
		for(IBoundedContext boundedContext : boundedContexts) {
			System.out.println("--" + boundedContext.getContextName() + "--");
			ExportBoundedContextTask boundedContextTask = new ExportBoundedContextTask(boundedContext);
			boundedContextTask.doExport();
			//exportFieldComposites(boundedContext.getContainingComposites());
		}
	}
	
	public static void exportFieldComposites(List<IFieldComposite> fieldComposites) {
		for(IFieldComposite fieldComposite : fieldComposites) {
			System.out.println(fieldComposite.getName());
		}
	}
}
