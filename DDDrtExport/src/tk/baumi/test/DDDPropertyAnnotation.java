package tk.baumi.test;

public @interface DDDPropertyAnnotation {
	boolean primaryKey() default false;
	String columnName();
	String columnType();
	String associativeTable() default "";
}
