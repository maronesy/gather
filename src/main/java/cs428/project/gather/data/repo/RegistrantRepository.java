package cs428.project.gather.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cs428.project.gather.data.model.Location;
import cs428.project.gather.data.model.Registrant;

/**
 * 
 * @author Team Gather
 * Repository for interacting with Registrant Model
 * 
 */
public interface RegistrantRepository  extends CrudRepository<Registrant, Long> {
	/**
	 * Returns the {@link Location} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Registrant findOne(Long id);
	
	/**
	 * Returns all registrant display name.
	 *
	 * @return a list of all registrant display name.
	 */
	@Query("SELECT u.displayName FROM Registrant u")
	List<String> findAllDisplayNames();
	
	/**
	 * Returns the registrant found by display name.
	 *
	 * @param displayName the display name to search for.
	 * @return the registrant with the display name
	 */
	Registrant findByDisplayName(String displayName);
	
	/**
	 * Returns the registrant found by email.
	 *
	 * @param email the email to search for.
	 * @return the registrant with the email
	 */
	Registrant findOneByEmail(String email);
}
