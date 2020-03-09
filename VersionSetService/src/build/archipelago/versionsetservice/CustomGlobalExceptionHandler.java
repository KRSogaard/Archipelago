package build.archipelago.versionsetservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public void springHandleNullPointerException(Exception ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}
