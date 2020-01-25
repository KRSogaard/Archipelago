package build.archipelago.packageservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthController {

    @GetMapping(value = {"/health-check"})
    @ResponseStatus(HttpStatus.OK)
    public String doHealthCheck() {
        return "Healthy";
    }
}
