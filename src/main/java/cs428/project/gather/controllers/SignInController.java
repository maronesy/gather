package cs428.project.gather.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs428.project.gather.data.SignInData;
import cs428.project.gather.data.model.Actor;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
import cs428.project.gather.utilities.RedirectPathHelper;

@Controller("signInController")
public class SignInController {
	// @Autowired
	// @Qualifier("signInDataValidator")
	// private SignInDataValidator signInDataValidator;
	//
	// @Autowired
	// @Qualifier("registrantDataAdapter")
	// private RegistrantDataAdapter registrantDataAdapter;
	//
	// @Autowired
	// @Qualifier("registeredUserDataAdapter")
	// private RegisteredUserDataAdapter registeredUserDataAdapter;
	//
	// @Autowired
	// @Qualifier("vendorDataAdapter")
	// private VendorDataAdapter vendorDataAdapter;
	//
	@ModelAttribute("signInData")
	public SignInData signInData() {
		SignInData signInData = new SignInData();

		return signInData;
	}

	@RequestMapping(value = "/sign-in", method = RequestMethod.GET)
	public String signIn(HttpServletRequest request) {
		String viewName = null;

		if (ActorTypeHelper.isAnonymousUser(request)) {
			viewName = "sign-in";
		} else {
			viewName = RedirectPathHelper.buildRedirectPath(request, "/invalid-request");
		}

		return viewName;
	}

	@RequestMapping(value = "/sign-in", method = RequestMethod.POST)
	public String signInProcessor(HttpServletRequest request, @ModelAttribute("signInData") SignInData signInData,
			BindingResult bindingResult) {
		String redirectPath = null;

		if (ActorTypeHelper.isAnonymousUser(request)) {
			// signInDataValidator.validate(signInData, bindingResult);

			if (bindingResult.hasErrors()) {
				redirectPath = "sign-in";
			} else {
				Date authenticationDateTime = new Date();

				if (authenticate(signInData, authenticationDateTime)) {
					String username = signInData.getUsername();

					// RegistrantType registrantType =
					// registrantDataAdapter.getRegistrantType(username);
					// if(registrantType == null)
					// {
					// // This indicates a problem with the sign-in data
					// validator or a data integrity problem.
					// throw new IllegalStateException("The registrant type is
					// not expected to be null.");
					// }
					//
					Registrant registrant = new Registrant(username, signInData.getPassword());

					//
					// if(RegistrantType.REGISTERED_USER.equals(registrantType))
					// {
					// registrant =
					// registeredUserDataAdapter.getRegisteredUser(username);
					// }
					// else if(RegistrantType.VENDOR.equals(registrantType))
					// {
					// registrant = vendorDataAdapter.getVendor(username);
					// }
					// else
					// {
					// // This is never expected.
					// throw new IllegalStateException("An unrecognized
					// registrant type was encountered.");
					// }

					ActorStateUtility.storeActorInSession(request, registrant);

					redirectPath = RedirectPathHelper.buildRedirectPath(request, "/");
				} else {
					String errorCode = "invalid." + SignInData.PASSWORD_FIELD_NAME;
					bindingResult.rejectValue(SignInData.PASSWORD_FIELD_NAME, errorCode,
							"The password is invalid.  Please enter a valid password.");

					redirectPath = "sign-in";
				}
			}
		} else {
			redirectPath = RedirectPathHelper.buildRedirectPath(request, "/invalid-request");
		}

		return redirectPath;
	}

	private boolean authenticate(SignInData signInData, Date authenticationDateTime) {
		// boolean authenticated = false;
		//
		String username = signInData.getUsername();
		String password = signInData.getPassword();
		//
		// if(registrantDataAdapter.authenticate(username, password,
		// authenticationDateTime))
		// {
		// authenticated = true;
		// }
		//
		// return authenticated;
		return (!username.equals("") && !password.equals(""));
	}
}
