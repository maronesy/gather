package cs428.project.gather.data;
import cs428.project.gather.validator.JoinEventDataValidator;
import org.springframework.validation.Errors;
import com.google.gson.*;


public class EventIdData {

	private Long eventId;

	public static EventIdData parseIn(String rawData, JoinEventDataValidator joinEventDataValidator, Errors errors) {
		System.out.println("rawData: " + rawData);
		EventIdData eventIdData = (new Gson()).fromJson(rawData, EventIdData.class);
		eventIdData.validate(joinEventDataValidator, errors);
		return eventIdData;
	}

	public void validate(JoinEventDataValidator joinEventDataValidator, Errors errors) {
		joinEventDataValidator.validate(this, errors);
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
