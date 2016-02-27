package cs428.project.gather;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
    
    @RequestMapping(value = "/signin")
    public String signin() {
        return "signin";
    }
    
    @RequestMapping(value = "/registrants")
    public String registrants() {
        return "registrants";
    }
    
}