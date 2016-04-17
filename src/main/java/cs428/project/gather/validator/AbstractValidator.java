package cs428.project.gather.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.validation.Validator;

public abstract class AbstractValidator implements Validator {
	// This ridiculously long regular expression conforms to the official RFC 2822 standard.
	// See http://tools.ietf.org/html/rfc2822#section-3.4.1 and http://www.regular-expressions.info/email.html for more information.
	private static final String INTERNET_MESSAGE_ADDRESS_REGULAR_EXPRESSION =
		"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-" +
		"\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a" +
		"-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0" +
		"-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	private static final Pattern INTERNET_MESSAGE_ADDRESS_PATTERN = Pattern.compile(INTERNET_MESSAGE_ADDRESS_REGULAR_EXPRESSION, (Pattern.CASE_INSENSITIVE + Pattern.DOTALL));

	// This expression is taken from the Apache Nutch source.
	// See http://lucene.apache.org/nutch/ for more information.
	private static final String URL_REGULAR_EXPRESSION = "([a-z][a-z0-9+.-]{1,120}:[a-z0-9/](([a-z0-9$_.+!*,;/?:@&~=-])|%[a-f0-9]{2}){1,333}(#([a-z0-9][a-z0-9$_.+!*,;/?:@&~=%-]{0,1000}))?)";

	private static final Pattern URL_PATTERN = Pattern.compile(URL_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	protected boolean matchesEmailAddressPattern(String emailAddress) {
		boolean matchesEmailAddressPattern = false;
		Matcher matcher = INTERNET_MESSAGE_ADDRESS_PATTERN.matcher(emailAddress);
		if (matcher.matches()) {
			matchesEmailAddressPattern = true;
		}

		return matchesEmailAddressPattern;
	}

	protected boolean matchesURLPattern(String urlValue) {
		boolean matchesURLPattern = false;
		Matcher matcher = URL_PATTERN.matcher(urlValue);
		if (matcher.matches()) {
			matchesURLPattern = true;
		}

		return matchesURLPattern;
	}
}
