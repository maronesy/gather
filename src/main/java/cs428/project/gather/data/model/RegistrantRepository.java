package cs428.project.gather.data.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RegistrantRepository  extends CrudRepository<Registrant, Long> {
	/**
	 * Returns the {@link Location} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Registrant findOne(Long id);
	
	List<Registrant> findByUsername(String username);
	
	List<Registrant> findByDisplayName(String displayName);
}
