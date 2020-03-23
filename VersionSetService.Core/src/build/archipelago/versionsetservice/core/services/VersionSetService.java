package build.archipelago.versionsetservice.core.services;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.common.exceptions.VersionSetExistsException;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.models.VersionSetRevision;

import java.util.List;
import java.util.Optional;

public interface VersionSetService {
    VersionSet get(String versionSetName);
    void create(String versionSetName, List<ArchipelagoPackage> targets, Optional<String> parent) throws VersionSetExistsException;
    String createRevision(String versionSetName, List<ArchipelagoBuiltPackage> parsePackages) throws VersionSetDoseNotExistsException;
    VersionSetRevision getRevision(String versionSetName, String revision) throws VersionSetDoseNotExistsException;
}
