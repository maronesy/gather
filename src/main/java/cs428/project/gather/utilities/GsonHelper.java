package cs428.project.gather.utilities;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * 
 * @author Team Gather
 * Utility class to help parse the raw JSON string into object.
 * 
 */
public class GsonHelper {
	
	/**
	 * Get a GSON parser to manage the date types as long values (timestamps)
	 * 
	 * @return a GSON parser
	 * 
	 */
	public static Gson getGson(){
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			// Register an adapter to manage the date types as long values
			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});
		return builder.create();
	}
	
}
