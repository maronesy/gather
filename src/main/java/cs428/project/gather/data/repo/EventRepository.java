package cs428.project.gather.data.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cs428.project.gather.data.model.Event;

/**
 * 
 * @author Team Gather
 * Repository for interacting with Event Model
 * 
 */
public interface EventRepository  extends CrudRepository<Event, Long> {

	/**
	 * Returns the {@link Event} with the given identifier.
	 *
	 * @param id the id to search for.
	 * @return
	 */
	Event findOne(Long id);

	/**
	 * Returns a list of events by name.
	 *
	 * @param name the event name.
	 * @return a list of found events by name
	 */
	List<Event> findByName(String name);
	
	/**
	 * Returns a list of events by description.
	 *
	 * @param description the event description.
	 * @return a list of found events by description
	 */
	List<Event> findByDescription(String description);

	/**
	 * Returns a list of events within an lat-long box.
	 *
	 * @param lowerLat lower latitude.
	 * @param upperLat upper latitude.
	 * @param lowerLon lower longitude.
	 * @param upperLat upper longitude.
	 * @return a list of found events by location
	 */
	@Query("SELECT DISTINCT e FROM Event e INNER JOIN e.location l WHERE l.latitude BETWEEN ?1 AND ?2 AND l.longitude BETWEEN ?3 AND ?4")
	List<Event> findByLocationWithin(double lowerLat, double uppLat, double lowerLon, double upperLon);

	/**
	 * Returns a list of events within an occurrence upper bound.
	 *
	 * @param upperBound occurrence upper bound.
	 * @return a list of found events by occurrence
	 */
	@Query("SELECT DISTINCT e FROM Event e INNER JOIN e.occurrences o WHERE o.timestamp > CURRENT_TIMESTAMP AND o.timestamp < ?1")
	List<Event> findByOccurrenceTimeWithin(Timestamp upperBound);

	/**
	 * Returns a list of events within an lat-long box and occurrence.
	 *
	 * @param lowerLat lower latitude.
	 * @param upperLat upper latitude.
	 * @param lowerLon lower longitude.
	 * @param upperLat upper longitude.
	 * @param upperBound occurrence upper bound.
	 * @return a list of found events by location and occurrence
	 */
	@Query("SELECT DISTINCT e FROM Event e INNER JOIN e.occurrences o INNER JOIN e.location l WHERE o.timestamp > CURRENT_TIMESTAMP AND o.timestamp < ?5 AND l.latitude BETWEEN ?1 AND ?2 AND l.longitude BETWEEN ?3 AND ?4")
	List<Event> findByLocationAndOccurrenceTimeWithin(double lowerLat, double uppLat, double lowerLon, double upperLon, Timestamp upperTime);

	/**
	 * Returns a list of events within an radius.
	 *
	 * @param latitude latitude.
	 * @param longitude latitude.
	 * @param radiusKm radius in KM.
	 * @return a list of found events by a radius on a given coordinate.
	 */
    @Query("SELECT DISTINCT e FROM Event e INNER JOIN e.location l WHERE SQRT(POWER((l.latitude - ?1)/(0.014554*1.60934), 2.0) + POWER((l.longitude - ?2)/(0.014457*1.60934), 2.0)) < ?3")
    List<Event> findByLocationWithinKmRadius(double latitude, double longitude, double radiusKm);
    
    /**
	 * Returns a list of events within an radius and occurrence.
	 *
	 * @param latitude latitude.
	 * @param longitude latitude.
	 * @param radiusKm radius in KM.
	 * @param upperBound occurrence upper bound.
	 * @return a list of found events by a radius on a given coordinate and occurrence.
	 */
    @Query("SELECT DISTINCT e FROM Event e INNER JOIN e.occurrences o INNER JOIN e.location l WHERE e.name = ?1 AND l.latitude = ?2 AND l.longitude = ?3 AND o.timestamp = ?4")
    List<Event> findByNameAndLocationAndTime(String name, double latitude, double longitude, Timestamp time);
}
