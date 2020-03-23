package build.archipelago.versionsetservice.client;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.exceptions.MissingTargetPackageException;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.common.exceptions.VersionSetExistsException;
import build.archipelago.versionsetservice.client.model.VersionSet;
import build.archipelago.versionsetservice.client.model.CreateVersionSetRequest;
import build.archipelago.versionsetservice.client.model.VersionSetRevision;

import java.util.List;

public interface VersionServiceClient {
    void createVersionSet(CreateVersionSetRequest request)
            throws VersionSetExistsException, VersionSetDoseNotExistsException, PackageNotFoundException;
    String createVersionRevision(String versionSetName, List<ArchipelagoBuiltPackage> packages)
            throws VersionSetDoseNotExistsException, MissingTargetPackageException, PackageNotFoundException;
    VersionSet getVersionSet(String versionSetName)
            throws VersionSetDoseNotExistsException;
    VersionSetRevision getVersionSetPackages(String versionSetName, String revisionId)
            throws VersionSetDoseNotExistsException;
}
