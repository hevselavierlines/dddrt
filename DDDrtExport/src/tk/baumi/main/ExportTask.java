package tk.baumi.main;

import java.io.File;
import java.util.List;

import javax.swing.BoundedRangeModel;

public class ExportTask {

	public static void exportBoundedContextsToJava(List<IBoundedContext> boundedContexts, File projectFolder,
			ITextReporter reporter) {
		for (IBoundedContext boundedContext : boundedContexts) {
//			System.out.println("--" + boundedContext.getContextName() + "--");
			ExportBoundedContextTask boundedContextTask = new ExportBoundedContextTask(boundedContext, projectFolder);
			boundedContextTask.setTextReporter(reporter);
			boundedContextTask.doJavaExport();
			// exportFieldComposites(boundedContext.getContainingComposites());
		}
	}

	public static String exportBoundedContextToDB(List<IFieldComposite> fieldComposites, List<IDDDRelation> relations) {
		StringBuffer sqlString = new StringBuffer();
		ExportDatabaseTask.createDeleteStatements(sqlString, fieldComposites);

		for (IFieldComposite fieldComposite : fieldComposites) {
			if (fieldComposite.getType() != CompositeType.ValueObject) {
				ExportDatabaseTask.exportFieldCompositeToDBTable(sqlString, fieldComposite);
			}
		}
		for (IDDDRelation relation : relations) {
			if (!relation.relationToValueObject()) {
				ExportDatabaseTask.exportRelationToDB(sqlString, relation);
			}
		}
		sqlString.append("\ncommit;");
		return sqlString.toString();
	}
}
