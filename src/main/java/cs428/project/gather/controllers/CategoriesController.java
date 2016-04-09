package cs428.project.gather.controllers;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import cs428.project.gather.data.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.utilities.*;

@Controller("CategoriesController")
public class CategoriesController extends AbstractGatherController {
	@RequestMapping(value = "/rest/categories", method = RequestMethod.GET)
	public ResponseEntity<RESTPaginatedResourcesResponseData<Category>> getCategories(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		List<Category> categories = categoryRepo.findAll();
		return RESTPaginatedResourcesResponseData.createResponse(request, categories);
	}
}
