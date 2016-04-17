package cs428.project.gather.data.form;

import cs428.project.gather.validator.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.Errors;
import com.google.gson.*;

public class SignInData {
	public static final String EMAIL_FIELD_NAME = "email";
	public static final String PASSWORD_FIELD_NAME = "password";

	private String email;
	private String password;

	public static SignInData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		SignInData signInData = (new Gson()).fromJson(rawData, SignInData.class);
		signInData.validate(validator, errors);
		return signInData;
	}

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

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(email);

		int hashCode = builder.toHashCode();

		return hashCode;
	}

	@Override
	public boolean equals(Object anotherObject) {
		boolean equal = false;

		if (anotherObject == this) {
			equal = true;
		} else if (anotherObject != null && anotherObject.getClass().equals(this.getClass())) {
			SignInData anotherSignInData = (SignInData) anotherObject;
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.email, anotherSignInData.email);
			equalsBuilder.append(this.password, anotherSignInData.password);
			equal = equalsBuilder.isEquals();
		}
		return equal;
	}
}
