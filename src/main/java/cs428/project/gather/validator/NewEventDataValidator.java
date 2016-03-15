package cs428.project.gather.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class NewEventDataValidator extends AbstractValidator{

	@Override
	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		// TODO Auto-generated method stub
		
	}

}
