package cs428.project.gather.data;

import cs428.project.gather.validator.*;

import java.util.*;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.builder.*;
import org.springframework.validation.Errors;
import com.google.gson.*;

public class RegistrationData {
	public static final String EMAIL_FIELD_NAME = "email";
	public static final String PASSWORD_FIELD_NAME = "password";
	public static final String OLD_PASSWORD_FIELD_NAME = "oldPassword";
	public static final String DISPLAY_NAME_FIELD_NAME = "displayName";

	private String email;
	private String password;
	private String oldPassword;
	private String displayName;
	private int defaultTimeWindow = 1;
	private int defaultZip = -1;
	private Set<String> preferences;

	public static RegistrationData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		RegistrationData registrationData = (new Gson()).fromJson(rawData, RegistrationData.class);
		registrationData.validate(validator, errors);
		return registrationData;
	}

	public void validate(AbstractValidator validator, Errors errors) {
		validator.validate(this, errors);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = StringUtils.trimToNull(password);
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = StringUtils.trimToNull(displayName);
	}

	public int getDefaultTimeWindow() {
		return defaultTimeWindow;
	}

	public void setDefaultTimeWindow(int defaultTimeWindow) {
		this.defaultTimeWindow = defaultTimeWindow;
	}

	public int getDefaultZip() {
		return defaultZip;
	}

	public void setDefaultZip(int defaultZip) {
		this.defaultZip = defaultZip;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = StringUtils.trimToNull(email);
	}

	public Set<String> getPreferences() {
		return preferences;
	}

	public void setPreferences(Set<String> preferences) {
		this.preferences = preferences;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(email);
		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object anotherObject) {
		boolean equal = false;
		if(anotherObject == this) {
			equal = true;
		} else if (anotherObject != null && anotherObject.getClass().equals(this.getClass())) {
			RegistrationData anotherUserRegistrationData = (RegistrationData)anotherObject;
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.email, anotherUserRegistrationData.email);
			equalsBuilder.append(this.password, anotherUserRegistrationData.password);
			equalsBuilder.append(this.displayName, anotherUserRegistrationData.displayName);
			equal = equalsBuilder.isEquals();
		}
		return equal;
	}
}

