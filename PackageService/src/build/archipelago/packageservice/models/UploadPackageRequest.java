package build.archipelago.packageservice.models;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadPackageRequest {
    private String name;
    private String version;
    private String hash;
    private MultipartFile buildArtifact;
    private String config;
}
