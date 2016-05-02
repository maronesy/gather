package cs428.project.gather.data.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cs428.project.gather.data.model.Category;
import cs428.project.gather.data.model.Event;

/**
 * 
 * @author Team Gather
 * Repository for interacting with Category Model
 * 
 */
public interface CategoryRepository  extends CrudRepository<Category, Long> {

	/**
	 * Returns the {@link Category} with the given identifier.
	 *
	 * @param id the id to search for.
	 * @return a found Category
	 */
	Category findOne(Long id);
	
	/**
	 * Returns a list of categories by name.
	 *
	 * @param name the category name.
	 * @return a list of found categories
	 */
	List<Category> findByName(String name);
	
	/**
	 * Returns the first category by name.
	 *
	 * @param name the category name.
	 * @return the first found category
	 */
	Category findOneByName(String name);
	
	/**
	 * Returns the all categories.
	 *
	 * @return all the categories
	 */
    List<Category> findAll();
}
