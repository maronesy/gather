package cs428.project.gather.data.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EventRepository  extends CrudRepository<Event, Long> {
	/**
	 * Returns the {@link Event} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Event findOne(Long id);
	
	List<Event> findByDescription(String description);
}
