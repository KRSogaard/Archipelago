package build.archipelago.packageservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthController {

    protected static final String HEALTHY_REPLY = "Healthy";
    protected static final String HEALTH_PATH = "/health-check";

    @GetMapping(value = {HEALTH_PATH})
    @ResponseStatus(HttpStatus.OK)
    public String doHealthCheck() {
        return HEALTHY_REPLY;
    }
}
