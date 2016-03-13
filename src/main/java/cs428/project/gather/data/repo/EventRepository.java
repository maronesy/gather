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
	
	@Query("SELECT e FROM Event e INNER JOIN e.location l WHERE l.latitude BETWEEN ?1 AND ?2 AND l.longitude BETWEEN ?3 AND ?4")
	List<Event> findByLocationWithin(double lowerLat, double uppLat, double lowerLon, double upperLon);
	
	@Query("SELECT e FROM Event e INNER JOIN e.occurrences o WHERE o.datetime > CURRENT_TIMESTAMP AND o.datetime < ?1")
	List<Event> findByOccurrenceTimeWithin(Timestamp upperBound);
	
	@Query("SELECT e FROM Event e INNER JOIN e.occurrences o INNER JOIN e.location l WHERE o.datetime > CURRENT_TIMESTAMP AND o.datetime < ?5 AND l.latitude BETWEEN ?1 AND ?2 AND l.longitude BETWEEN ?3 AND ?4")
	List<Event> findByLocationAndOccurrenceTimeWithin(double lowerLat, double uppLat, double lowerLon, double upperLon, Timestamp upperTime);


	//@Query("SELECT * FROM Event WHERE SQRT(
//  POW(e.occurrences.location.latitude - ?1, 2) +
//  POW(e.occurrences.location.longitude - ?2, 2)
//  ) < ?3")
	// public List<Event> findEventsWithinKmRange(double latitude, double longitude, double radius_km);

}
