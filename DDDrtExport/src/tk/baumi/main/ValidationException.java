package tk.baumi.main;

public class ValidationException extends Exception {

	private IFieldComposite fieldComposite;
	private IBoundedContext boundedContext;
	private ExportProperty property;
	private ExportMethod method;
	private String message;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8175473227303307288L;
	
	public ValidationException(String message, IFieldComposite fieldComposite) {
		super(message);
		this.fieldComposite = fieldComposite;
		this.message = message;
	}
	
	public ValidationException(String message, ExportProperty exportProperty) {
		super(message);
		this.property = exportProperty;
		this.message = message;
	}
}
