package build.archipelago.kauai.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class PackagesControler {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
