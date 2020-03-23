package build.archipelago.packageservice.client.rest;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.client.PackageServiceClient;
import build.archipelago.packageservice.client.models.CreatePackageRequest;
import build.archipelago.packageservice.client.models.GetPackageBuildResponse;
import build.archipelago.packageservice.client.models.GetPackageResponse;
import build.archipelago.packageservice.client.models.PackageBuildsResponse;
import build.archipelago.packageservice.client.models.PackageVerificationResult;
import build.archipelago.packageservice.client.models.UploadPackageRequest;
import build.archipelago.packageservice.client.rest.models.RestArtifactUploadResponse;
import build.archipelago.packageservice.client.rest.models.RestCreatePackageRequest;
import build.archipelago.packageservice.client.rest.models.RestGetPackageBuildResponse;
import build.archipelago.packageservice.client.rest.models.RestGetPackageResponse;
import build.archipelago.packageservice.client.rest.models.RestPackageBuildsResponse;
import build.archipelago.packageservice.client.rest.models.RestVerificationRequest;
import build.archipelago.packageservice.client.rest.models.RestVerificationResponse;
import build.archipelago.common.exceptions.PackageExistsException;
import build.archipelago.common.exceptions.PackageNotFoundException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RestPackageServiceClient implements PackageServiceClient {

    private RestTemplate restTemplate;
    private String endpoint;

    public RestPackageServiceClient(String endpoint) {
        restTemplate = new RestTemplate();
        if (endpoint.endsWith("/")) {
            this.endpoint = endpoint.substring(0, endpoint.length() - 2);
        } else {
            this.endpoint = endpoint;
        }
    }

    @Override
    public void createPackage(CreatePackageRequest request) throws PackageExistsException {
        Preconditions.checkNotNull(request);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(request.getName()));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(request.getDescription()));

        RestCreatePackageRequest restRequest = new RestCreatePackageRequest(
                request.getName(),
                request.getDescription()
        );

        try {
            restTemplate.postForEntity(endpoint + "/package", restRequest, ResponseEntity.class);
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.CONFLICT.equals(exp.getStatusCode())) {
                throw new PackageExistsException(request.getName());
            }
            throw new RuntimeException("Was unable to create the package", exp);
        }
    }

    @Override
    public GetPackageResponse getPackage(String name) throws PackageNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        try {
            RestGetPackageResponse response = restTemplate.getForObject(endpoint + "/package/" + name,
                    RestGetPackageResponse.class);

            ImmutableList.Builder<GetPackageResponse.Version> versions = ImmutableList.builder();
            response.getVersions().forEach(x -> versions.add(new GetPackageResponse.Version(
                    x.getVersion(), x.getLatestBuildHash(), Instant.ofEpochMilli(x.getLatestBuildTime())
            )));

            return GetPackageResponse.builder()
                    .name(response.getName())
                    .description(response.getDescription())
                    .created(Instant.ofEpochMilli(response.getCreated()))
                    .versions(versions.build())
                    .build();
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new PackageNotFoundException(name);
            }
            throw new RuntimeException("Was unable to fetch package " + name, exp);
        }
    }

    @Override
    public PackageBuildsResponse getPackageBuilds(ArchipelagoPackage pkg) throws PackageNotFoundException {
        Preconditions.checkNotNull(pkg);

        try {
            RestPackageBuildsResponse response = restTemplate.getForObject(endpoint +
                            "/package/" + pkg.getName() + "/" + pkg.getVersion(),
                    RestPackageBuildsResponse.class);

            ImmutableList.Builder<PackageBuildsResponse.Build> builds = ImmutableList.builder();
            response.getBuilds().forEach(x -> builds.add(new PackageBuildsResponse.Build(
                    x.getHash(), Instant.ofEpochMilli(x.getCreated())
            )));

            return PackageBuildsResponse.builder()
                    .builds(builds.build())
                    .build();
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new PackageNotFoundException(pkg);
            }
            throw new RuntimeException("Was unable to fetch package " + pkg.getNameVersion(), exp);
        }
    }

    @Override
    public GetPackageBuildResponse getPackageBuild(ArchipelagoBuiltPackage pkg) throws PackageNotFoundException {
        Preconditions.checkNotNull(pkg);

        try {
            RestGetPackageBuildResponse response = restTemplate.getForObject(endpoint +
                            "/package/" + pkg.getName() + "/" + pkg.getVersion() + "/" + pkg.getHash(),
                    RestGetPackageBuildResponse.class);

            return GetPackageBuildResponse.builder()
                    .hash(response.getHash())
                    .created(Instant.ofEpochMilli(response.getCreated()))
                    .config(response.getConfig())
                    .build();
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new PackageNotFoundException(pkg);
            }
            throw new RuntimeException("Was unable to fetch package " + pkg.toString(), exp);
        }
    }

    @Override
    public PackageVerificationResult<ArchipelagoPackage> verifyPackagesExists(List<ArchipelagoPackage> packages) {
        Preconditions.checkNotNull(packages);
        Preconditions.checkArgument(packages.size() > 0);

        RestVerificationRequest restRequest = new RestVerificationRequest(
                packages.stream().map(ArchipelagoPackage::getNameVersion).collect(Collectors.toList()));

        try {
            RestVerificationResponse res = restTemplate.postForObject(endpoint + "/package/verify-packages",
                    restRequest, RestVerificationResponse.class);

            return PackageVerificationResult.<ArchipelagoPackage>builder()
                    .missingPackages(ImmutableList.copyOf(
                            res.getMissing().stream().map(ArchipelagoPackage::parse).collect(Collectors.toList())))
                    .build();
        } catch (HttpClientErrorException exp) {
            throw new RuntimeException("Was unable to verify packages", exp);
        }
    }

    @Override
    public PackageVerificationResult<ArchipelagoBuiltPackage> verifyBuildsExists(List<ArchipelagoBuiltPackage> packages) {
        Preconditions.checkNotNull(packages);
        Preconditions.checkArgument(packages.size() > 0);

        RestVerificationRequest restRequest = new RestVerificationRequest(
                packages.stream().map(ArchipelagoBuiltPackage::toString).collect(Collectors.toList()));

        try {
            RestVerificationResponse res = restTemplate.postForObject(endpoint + "/package/verify-builds",
                    restRequest, RestVerificationResponse.class);

            return PackageVerificationResult.<ArchipelagoBuiltPackage>builder()
                    .missingPackages(ImmutableList.copyOf(
                            res.getMissing().stream().map(ArchipelagoBuiltPackage::parse).collect(Collectors.toList())))
                    .build();
        } catch (HttpClientErrorException exp) {
            throw new RuntimeException("Was unable to verify packages", exp);
        }
    }

    @Override
    public String uploadBuiltArtifact(UploadPackageRequest request, Path file) throws PackageNotFoundException {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.getPkg());
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(Files.exists(file), "File did not exists");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("buildArtifact", new FileSystemResource(file));
        body.add("config", request.getConfig());

        String url = String.format("%s/artifact/%s/%s",
                endpoint, request.getPkg().getName(), request.getPkg().getVersion());
        try {
            RestArtifactUploadResponse response = restTemplate.postForObject(url,
                    new HttpEntity<>(body, headers), RestArtifactUploadResponse.class);
            return response.getHash();
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new PackageNotFoundException(request.getPkg());
            }
            throw new RuntimeException("Was unable to upload package " + request.getPkg(), exp);
        }
    }

    @Override
    public Path getBuildArtifact(ArchipelagoBuiltPackage pkg, Path directory) throws PackageNotFoundException, IOException {
        Preconditions.checkNotNull(pkg, "Name and Version is required");
        Preconditions.checkNotNull(directory, "A save location is required");

        if (!Files.isDirectory(directory)) {
            log.info("Creating directory \"%s\"", directory.toString());
            Files.createDirectories(directory);
        }

        String url = String.format("%s/artifact/%s/%s/%s",
                endpoint, pkg.getName(), pkg.getVersion(), pkg.getHash());
        try {
            byte[] data = restTemplate.getForObject(url, byte[].class);
            Path filePath = Paths.get(
                    directory.toString(),
                    String.format("%s.zip", java.util.UUID.randomUUID().toString()));
            log.debug("writing {} byes to \"{}\"", data.length, filePath);
            Files.write(filePath, data);
            return filePath;
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new PackageNotFoundException(pkg);
            }
            throw new RuntimeException("Was unable to fetch artifact for package " + pkg, exp);
        }

    }
}
