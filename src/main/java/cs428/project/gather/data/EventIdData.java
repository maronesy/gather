package cs428.project.gather.data;
import cs428.project.gather.validator.*;
import org.springframework.validation.Errors;
import com.google.gson.*;


public class EventIdData {

	private Long eventId;

	public static EventIdData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		EventIdData eventIdData = (new Gson()).fromJson(rawData, EventIdData.class);
		eventIdData.validate(validator, errors);
		return eventIdData;
	}

	public void validate(AbstractValidator validator, Errors errors) {
		validator.validate(this, errors);
	}

	public Long getEventId()
	{
		return eventId;
	}

	public void setEventId(Long id)
	{
		this.eventId = id;
	}
}
