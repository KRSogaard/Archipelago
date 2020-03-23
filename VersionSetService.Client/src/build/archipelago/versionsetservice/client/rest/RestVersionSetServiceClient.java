package build.archipelago.versionsetservice.client.rest;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.exceptions.MissingTargetPackageException;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.common.exceptions.VersionSetExistsException;
import build.archipelago.versionsetservice.client.VersionServiceClient;
import build.archipelago.versionsetservice.client.model.VersionSet;
import build.archipelago.versionsetservice.client.model.CreateVersionSetRequest;
import build.archipelago.versionsetservice.client.model.VersionSetRevision;
import build.archipelago.versionsetservice.client.rest.models.RestCreateVersionSetRequest;
import build.archipelago.versionsetservice.client.rest.models.RestCreateVersionSetRevisionRequest;
import build.archipelago.versionsetservice.client.rest.models.RestCreateVersionSetRevisionResponse;
import build.archipelago.versionsetservice.client.rest.models.RestVersionSetResponse;
import build.archipelago.versionsetservice.client.rest.models.RestVersionSetRevisionResponse;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class RestVersionSetServiceClient implements VersionServiceClient {

    private RestTemplate restTemplate;
    private String endpoint;

    public RestVersionSetServiceClient(String endpoint) {
        restTemplate = new RestTemplate();
        if (endpoint.endsWith("/")) {
            this.endpoint = endpoint.substring(0, endpoint.length() - 2);
        } else {
            this.endpoint = endpoint;
        }
    }

    @Override
    public void createVersionSet(CreateVersionSetRequest request)
            throws VersionSetExistsException, VersionSetDoseNotExistsException, PackageNotFoundException {
        Preconditions.checkNotNull(request);
        request.validate();

        RestCreateVersionSetRequest restRequset = new RestCreateVersionSetRequest(
                request.getName(),
                request.getTargets().stream().map(ArchipelagoPackage::getNameVersion).collect(Collectors.toList()),
                request.getParent() != null && request.getParent().isPresent() ? request.getParent().get() : null
        );

        try {
            restTemplate.postForEntity(endpoint + "/version-sets", restRequset, ResponseEntity.class);
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.CONFLICT.equals(exp.getStatusCode())) {
                throw new VersionSetExistsException(request.getName());
            } else if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                if (request.getParent() == null || request.getParent().isEmpty()) {
                    log.error("Create version set returned version set not found status code, " +
                            "but no parent this should not happen.", exp);
                    throw new RuntimeException(exp);
                }
                throw new VersionSetDoseNotExistsException(request.getParent().get());
            } else if (HttpStatus.NOT_ACCEPTABLE.equals(exp.getStatusCode())) {
                throw new PackageNotFoundException(exp.getMessage());
            }
            throw new RuntimeException("Was unable to create the version set", exp);
        }
    }

    @Override
    public String createVersionRevision(String versionSetName, List<ArchipelagoBuiltPackage> packages)
            throws VersionSetDoseNotExistsException, MissingTargetPackageException, PackageNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionSetName), "Version set name is required");
        Preconditions.checkArgument(packages != null && packages.size() > 0, "Packages are required");

        RestCreateVersionSetRevisionRequest restRequset = new RestCreateVersionSetRevisionRequest(
                packages.stream().map(ArchipelagoBuiltPackage::toString).collect(Collectors.toList())
        );

        try {
            RestCreateVersionSetRevisionResponse response = restTemplate.postForObject(
                    endpoint + "/version-sets/" + versionSetName, restRequset,
                    RestCreateVersionSetRevisionResponse.class);

            return response.getRevisionId();
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new VersionSetDoseNotExistsException(versionSetName);
            } else if (HttpStatus.NOT_ACCEPTABLE.equals(exp.getStatusCode())) {
                throw new PackageNotFoundException(exp.getMessage());
            } else if (HttpStatus.PRECONDITION_FAILED.equals(exp.getStatusCode())) {
                throw new MissingTargetPackageException();
            }
            throw new RuntimeException("Was unable to create the version set revision", exp);
        }
    }

    @Override
    public VersionSet getVersionSet(String versionSetName) throws VersionSetDoseNotExistsException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionSetName), "Version set name is required");

        try {
            String url = String.format("%s//version-sets/%s", endpoint, versionSetName);
            RestVersionSetResponse response = restTemplate.getForObject(url, RestVersionSetResponse.class);

            return VersionSet.builder()
                    .name(response.getName())
                    .created(Instant.ofEpochMilli(response.getCreated()))
                    .parent(response.getParent())
                    .targets(response.getTargets().stream().map(ArchipelagoPackage::parse).collect(Collectors.toList()))
                    .revisions(response.getRevisions().stream().map(x -> new VersionSet.Revision(
                            x.getRevisionId(), Instant.ofEpochMilli(x.getCreated()))).collect(Collectors.toList()))
                    .latestRevision(response.getLatestRevision())
                    .latestRevisionCreated(response.getLatestRevisionCreated().isPresent() ?
                            Optional.of(Instant.ofEpochMilli(response.getLatestRevisionCreated().get())) :
                            Optional.empty())
                    .build();
        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new VersionSetDoseNotExistsException(versionSetName);
            }
            throw new RuntimeException("Was unable to fetch version set " + versionSetName, exp);
        }
    }

    @Override
    public VersionSetRevision getVersionSetPackages(String versionSetName, String revisionId) throws VersionSetDoseNotExistsException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionSetName), "Version set name is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(revisionId), "Version set revision is required");


        try {
            String url = String.format("%s//version-sets/%s/%s", endpoint, versionSetName, revisionId);
            RestVersionSetRevisionResponse response = restTemplate.getForObject(url, RestVersionSetRevisionResponse.class);

            return VersionSetRevision.builder()
                    .created(Instant.ofEpochMilli(response.getCreated()))
                    .packages(response.getPackages().stream().map(ArchipelagoBuiltPackage::parse).collect(Collectors.toList()))
                    .build();

        } catch (HttpClientErrorException exp) {
            if (HttpStatus.NOT_FOUND.equals(exp.getStatusCode())) {
                throw new VersionSetDoseNotExistsException(versionSetName);
            }
            throw new RuntimeException("Was unable to fetch version set " + versionSetName, exp);
        }
    }
}
