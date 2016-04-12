package cs428.project.gather.validator;


import cs428.project.gather.data.RegistrationData;
import cs428.project.gather.data.repo.RegistrantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class RegistrationDataValidator extends AbstractValidator
{
	@Autowired
	private RegistrantRepository registrantDataAdapter;

	@Override
	public boolean supports(Class<?> targetClass)
	{
		boolean supported = false;

		if(RegistrationData.class.equals(targetClass))
		{
			supported = true;
		}

		return supported;
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		
		RegistrationData userRegistrationData = (RegistrationData)target;

		if(userRegistrationData == null)
		{
			throw new IllegalArgumentException("The user registration data cannot be null.");
		}
		else
		{
			validatePassword(userRegistrationData, errors);
			validateDisplayName(userRegistrationData, errors);
			validateEmailAddress(userRegistrationData, errors);

		}
	}

	private void validatePassword(RegistrationData userRegistrationData, Errors errors)
	{
		String password = userRegistrationData.getPassword();
		if(password == null)
		{
			String message = "Field required-" + RegistrationData.PASSWORD_FIELD_NAME;
			errors.reject("-1", message+":Password is a required field.");
		}
		else if(password.length() > 64)
		{
			String message = "Field invalid-" + RegistrationData.PASSWORD_FIELD_NAME;
			errors.reject("-2", message+":The password length must be 64 characters or less.");
		}
	}

	private void validateDisplayName(RegistrationData userRegistrationData, Errors errors)
	{

		String displayName = userRegistrationData.getDisplayName();

		if(displayName == null)
		{
			String message = "Field required-" + RegistrationData.DISPLAY_NAME_FIELD_NAME;
			errors.reject("-1", message+":Display name is a required field.");
		}
		else if(displayName.length() > 64)
		{
			String message = "Field invalid-" + RegistrationData.DISPLAY_NAME_FIELD_NAME;
			errors.reject("-2", message+":The display name length must be 64 characters or less.");
		}
		else if(registrantDataAdapter.findByDisplayName(displayName)!=null)
		{

			String message = "Field invalid-" + RegistrationData.DISPLAY_NAME_FIELD_NAME;
			errors.reject("-4",message+":The display name already exists.  Please enter another display name.");//, "The display name already exists.  Please enter another display name.");
			
		}
	}

	private void validateEmailAddress(RegistrationData userRegistrationData, Errors errors)
	{
		String emailAddress = userRegistrationData.getEmail();
		if(emailAddress == null)
		{
			String message = "Field required-" + RegistrationData.EMAIL_FIELD_NAME;
			errors.reject("-1", message+":Email address is a required field.");
		}
		else if(emailAddress.length() > 128)
		{
			String message = "Field invalid-" + RegistrationData.EMAIL_FIELD_NAME;
			errors.reject("-2", message+":The email address length must be 128 characters or less.");
		}
		else if(matchesEmailAddressPattern(emailAddress) == false)
		{
			String message = "Field invalid-" + RegistrationData.EMAIL_FIELD_NAME;
			errors.reject("-3", message+":Please enter a valid email address.");
		}
		else if(registrantDataAdapter.findOneByEmail(emailAddress)!=null)
		{
			String message = "Field invalid-" + RegistrationData.EMAIL_FIELD_NAME;
			errors.reject("-4", message+":The email address already exists.  Please enter another email address.");
		}
	}
}
