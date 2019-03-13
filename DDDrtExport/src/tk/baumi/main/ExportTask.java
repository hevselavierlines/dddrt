package tk.baumi.main;

import java.io.File;
import java.util.List;

import javax.swing.BoundedRangeModel;

public class ExportTask {
	
	public static void exportBoundedContextsToJava(List<IBoundedContext> boundedContexts) {
		for(IBoundedContext boundedContext : boundedContexts) {
//			System.out.println("--" + boundedContext.getContextName() + "--");
			ExportBoundedContextTask boundedContextTask = new ExportBoundedContextTask(boundedContext);
			boundedContextTask.doJavaExport();
			//exportFieldComposites(boundedContext.getContainingComposites());
		}
	}
	
	public static void exportBoundedContextToDB(List<IBoundedContext> boundedContexts) {
		StringBuffer sqlString = new StringBuffer();
		//TODO comments for the start
		
		for(IBoundedContext boundedContext : boundedContexts) {
			ExportBoundedContextTask boundedContextTask = new ExportBoundedContextTask(boundedContext);
			boundedContextTask.doDatabaseExport(sqlString);
		}
		System.out.println(sqlString.toString());
	}
}
