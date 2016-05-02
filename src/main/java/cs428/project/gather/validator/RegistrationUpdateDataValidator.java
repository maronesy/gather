package cs428.project.gather.validator;


import cs428.project.gather.data.form.RegistrationData;
import cs428.project.gather.data.repo.RegistrantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * 
 * @author Team Gather
 * 
 * This class validates the password, display name, and email address and old password of RegistrationData when a user requests to update their information.
 *
 */
@Component
public class RegistrationUpdateDataValidator extends AbstractValidator {
	@Override
	public boolean supports(Class<?> targetClass) {
		return RegistrationData.class.equals(targetClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RegistrationData userRegistrationData = (RegistrationData)target;

		if (userRegistrationData == null) {
			throw new IllegalArgumentException("The user registration data cannot be null.");
		} else {
			validatePassword(userRegistrationData, errors);
			validateDisplayName(userRegistrationData, errors);
			validateEmailAddress(userRegistrationData, errors);
			validateOldPassword(userRegistrationData, errors);
		}
	}

	private void validatePassword(RegistrationData userRegistrationData, Errors errors) {
		String password = userRegistrationData.getPassword();
		if (password == null) return; // Skip check if password is null

		if(password.length() > 64) {
			String message = "Field invalid-" + RegistrationData.PASSWORD_FIELD_NAME;
			errors.reject("-2", message+":The password length must be 64 characters or less.");
		}
	}

	private void validateDisplayName(RegistrationData userRegistrationData, Errors errors) {
		String displayName = userRegistrationData.getDisplayName();
		if (displayName == null) return; // Skip check if displayName is null

		if (displayName.length() > 64) {
			String message = "Field invalid-" + RegistrationData.DISPLAY_NAME_FIELD_NAME;
			errors.reject("-2", message+":The display name length must be 64 characters or less.");

		}
	}

	private void validateEmailAddress(RegistrationData userRegistrationData, Errors errors) {
		String emailAddress = userRegistrationData.getEmail();
		if (emailAddress == null) return; // Skip check if emailAddress is null

		if (emailAddress.length() > 128) {
			String message = "Field invalid-" + RegistrationData.EMAIL_FIELD_NAME;
			errors.reject("-2", message+":The email address length must be 128 characters or less.");

		} else if (matchesEmailAddressPattern(emailAddress) == false) {
			String message = "Field invalid-" + RegistrationData.EMAIL_FIELD_NAME;
			errors.reject("-3", message+":Please enter a valid email address.");
		}
	}

	private void validateOldPassword(RegistrationData userRegistrationData, Errors errors) {
		// Users only need to enter their old password when changing to a new password
		if (userRegistrationData.getPassword() != null && userRegistrationData.getOldPassword() == null) {
			String message = "Field required-" + RegistrationData.OLD_PASSWORD_FIELD_NAME;
			errors.reject("-1", message + ":oldPassword is a required field when updating passwords.");
		}
	}
}
