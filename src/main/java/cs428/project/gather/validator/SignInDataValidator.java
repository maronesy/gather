package cs428.project.gather.validator;


import cs428.project.gather.data.SignInData;
import cs428.project.gather.data.repo.RegistrantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class SignInDataValidator extends AbstractValidator
{
	@Autowired
	private RegistrantRepository registrantDataAdapter;

	@Override
	public boolean supports(Class<?> targetClass)
	{
		boolean supported = false;

		if(SignInData.class.equals(targetClass))
		{
			supported = true;
		}

		return supported;
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		
		SignInData userSignInData = (SignInData)target;

		if(userSignInData == null)
		{
			throw new IllegalArgumentException("The user registration data cannot be null.");
		}
		else
		{
			validatePassword(userSignInData, errors);
			validateEmailAddress(userSignInData, errors);

		}
	}

	private void validatePassword(SignInData userSignInData, Errors errors)
	{
		String password = userSignInData.getPassword();
		if(password == null)
		{
			String message = "Field required-" + SignInData.PASSWORD_FIELD_NAME;
			errors.reject("-1", message+":Password is a required field.");
		}
		else if(password.length() > 64)
		{
			String message = "Field invalid-" + SignInData.PASSWORD_FIELD_NAME;
			errors.reject("-2", message+":The password length must be 64 characters or less.");
		}
	}

	private void validateEmailAddress(SignInData userSignInData, Errors errors)
	{
		String emailAddress = userSignInData.getEmail();
		if(emailAddress == null)
		{
			String message = "Field required-" + SignInData.EMAIL_FIELD_NAME;
			errors.reject("-1", message+":Email address is a required field.");
		}
		else if(emailAddress.length() > 128)
		{
			String message = "Field invalid-" + SignInData.EMAIL_FIELD_NAME;
			errors.reject("-2", message+":The email address length must be 128 characters or less.");
		}
		else if(matchesEmailAddressPattern(emailAddress) == false)
		{
			String message = "Field invalid-" + SignInData.EMAIL_FIELD_NAME;
			errors.reject("-3", message+":Please enter a valid email address.");
		}
		else if(registrantDataAdapter.findOneByEmail(emailAddress)==null)
		{
			String message = "Field invalid-" + SignInData.EMAIL_FIELD_NAME;
			errors.reject("-5", message+":The email address doesn't exist.  Please enter another email address.");
		}
	}
}
