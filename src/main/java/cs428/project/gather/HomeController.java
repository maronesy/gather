package cs428.project.gather;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
    
    @RequestMapping(value = "/register")
    public String register() {
        return "register";
    }
    
    @RequestMapping(value = "/registrants")
    public String registrants() {
        return "registrants";
    }
    
}