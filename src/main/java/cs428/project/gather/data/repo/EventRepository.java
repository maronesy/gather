package cs428.project.gather.data.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cs428.project.gather.data.model.Event;

public interface EventRepository  extends CrudRepository<Event, Long> {
	/**
	 * Returns the {@link Event} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Event findOne(Long id);
	
	List<Event> findByDescription(String description);
	
	List<Event> findByLocationLatitudeIsBetweenAndLocationLongitudeIsBetween(double lowerLat, double uppLat, double lowerLon, double upperLon);
	
	@Query("SELECT e FROM Event e INNER JOIN e.occurrences o WHERE o.datetime > CURRENT_TIMESTAMP AND o.datetime < ?1")
	List<Event> findByOccurrenceWithinTime(Timestamp upperBound);
}
