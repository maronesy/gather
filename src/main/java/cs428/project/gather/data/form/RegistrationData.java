package cs428.project.gather.data.form;

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
	private Boolean showEventsAroundZipCode;

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

	public Boolean getShowEventsAroundZipCode() {
		return showEventsAroundZipCode;
	}

	public void setShowEventsAroundZipCode(Boolean showEventsAroundZipCode) {
		this.showEventsAroundZipCode = showEventsAroundZipCode;
	}
}

