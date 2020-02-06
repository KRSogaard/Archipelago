package build.archipelago.packageservice;

import build.archipelago.packageservice.core.exceptions.PackageArtifactExistsException;
import build.archipelago.packageservice.core.exceptions.PackageArtifactNotFoundException;
import build.archipelago.packageservice.core.exceptions.PackageExistsException;
import build.archipelago.packageservice.core.exceptions.PackageNotFoundException;
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

    @ExceptionHandler(PackageNotFoundException.class)
    public void springHandlePackageNotFoundException(Exception ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(PackageArtifactExistsException.class)
    public void springHandlePackageArtifactExistsException(Exception ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(PackageExistsException.class)
    public void springHandlePackageExistsException(Exception ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
}
