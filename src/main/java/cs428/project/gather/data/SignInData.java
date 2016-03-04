package cs428.project.gather.data;

import org.apache.commons.lang3.StringUtils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class SignInData
{
	public static final String USERNAME_FIELD_NAME = "username";
	public static final String PASSWORD_FIELD_NAME = "password";

	private String username;
	private String password;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = StringUtils.trimToNull(username);
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = StringUtils.trimToNull(password);
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(username);

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
			SignInData anotherSignInData = (SignInData)anotherObject;

			EqualsBuilder equalsBuilder = new EqualsBuilder();

			equalsBuilder.append(this.username, anotherSignInData.username);
			equalsBuilder.append(this.password, anotherSignInData.password);

			equal = equalsBuilder.isEquals();
		}

		return equal;
	}
}
