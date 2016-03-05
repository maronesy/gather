package cs428.project.gather.data;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RegistrationData {

	public static final String EMAIL_FIELD_NAME = "email";
	public static final String PASSWORD_FIELD_NAME = "password";
	public static final String DISPLAY_NAME_FIELD_NAME = "displayName";

	private String email;
	private String password;
	private String displayName;
	
	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = StringUtils.trimToNull(password);
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = StringUtils.trimToNull(displayName);
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = StringUtils.trimToNull(email);
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(email);

		int hashCode = builder.toHashCode();

		return hashCode;
	}

	@Override
	public boolean equals(Object anotherObject)
	{
		boolean equal = false;

		if(anotherObject == this)
		{
			equal = true;
		}
		else if(anotherObject != null && anotherObject.getClass().equals(this.getClass()))
		{
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

