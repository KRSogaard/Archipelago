package build.archipelago.versionsetservice.core.utils;

import java.util.UUID;

public class RevisionUtil {
    public static String getRandomRevisionId() {
        return UUID.randomUUID().toString().split("-", 2)[0];
    }
}
