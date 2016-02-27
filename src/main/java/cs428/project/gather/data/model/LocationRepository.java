package cs428.project.gather.data.model;

import org.springframework.data.repository.CrudRepository;

public interface LocationRepository  extends CrudRepository<Location, Long> {
	/**
	 * Returns the {@link Location} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Location findOne(Long id);
}
