package cs428.project.gather.data.repo;

import org.springframework.data.repository.CrudRepository;

import cs428.project.gather.data.model.Location;

public interface LocationRepository  extends CrudRepository<Location, Long> {
	/**
	 * Returns the {@link Location} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Location findOne(Long id);
}
