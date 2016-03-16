package cs428.project.gather.data.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cs428.project.gather.data.model.Category;
import cs428.project.gather.data.model.Event;

public interface CategoryRepository  extends CrudRepository<Category, Long> {

	/**
	 * Returns the {@link Category} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Category findOne(Long id);
	
	List<Category> findByName(String name);

}
