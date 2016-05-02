package cs428.project.gather.data.form;

import cs428.project.gather.validator.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.Errors;
import com.google.gson.*;

/**
 * 
 * @author Team Gather
 * This class represents the data object for signing in 
 * 
 */
public class SignInData {
	/**
	 * Public static names for validator getting the field names
	 */
	public static final String EMAIL_FIELD_NAME = "email";
	public static final String PASSWORD_FIELD_NAME = "password";

	private String email;
	private String password;

	/**
	 * Parse the raw JSON data in String and validate the data, then set the 
	 * Error code accordingly.
	 * 
	 * @param rawData: The raw JSON data in String
	 * @param validator: The validator object to validate the input data
	 * @param errors: The error object to pass to the validator for different error code 
	 * @return: A paginated bad request response based on the binding result.
	 * 
	 */
	public static SignInData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		SignInData signInData = (new Gson()).fromJson(rawData, SignInData.class);
		signInData.validate(validator, errors);
		return signInData;
	}

	/**
	 * Validate this object and save the Error status
	 * 
	 * @param validator: The validator object to validate the input data
	 * @param errors: The error object to pass to the validator for different error code 
	 * 
	 */
	public void validate(AbstractValidator validator, Errors errors) {
		validator.validate(this, errors);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = StringUtils.trimToNull(email);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = StringUtils.trimToNull(password);
	}
}
