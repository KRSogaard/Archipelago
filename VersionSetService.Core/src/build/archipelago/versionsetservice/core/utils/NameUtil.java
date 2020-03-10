package build.archipelago.versionsetservice.core.utils;

import java.util.regex.Pattern;

public class NameUtil {
    private static final Pattern namePattern = Pattern.compile("^[\\w-]{3,}$");

    public static boolean validateVersionSetName(final String v) {
        return namePattern.matcher(v).find();
    }
}
