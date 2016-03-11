package cs428.project.gather.data.repo;

import java.util.List;

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

	//@Query("SELECT * FROM Event WHERE SQRT(
//  POW(e.occurrences.location.latitude - ?1, 2) +
//  POW(e.occurrences.location.longitude - ?2, 2)
//  ) < ?3")
	// public List<Event> findEventsWithinKmRange(double latitude, double longitude, double radius_km);

}
