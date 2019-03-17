package tk.baumi.main;

import java.util.List;

public interface IValidateable {
	void updateValidationStatus(List<ValidationException> validationExceptions);
}
