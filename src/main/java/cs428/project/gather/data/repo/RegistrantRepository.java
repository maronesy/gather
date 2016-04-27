package cs428.project.gather.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cs428.project.gather.data.model.Location;
import cs428.project.gather.data.model.Registrant;

public interface RegistrantRepository  extends CrudRepository<Registrant, Long> {
	/**
	 * Returns the {@link Location} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	@Query("SELECT u.displayName FROM Registrant u")
	List<String> findAllDisplayNames();
	
	Registrant findOne(Long id);
	Registrant findByDisplayName(String displayName);
	Registrant findOneByEmail(String email);
}
