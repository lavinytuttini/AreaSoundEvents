package me.lavinytuttini.areasoundevents.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ServerVersion {
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3;

    private static final Pattern VERSION_PATTERN = Pattern.compile("v(\\d+)_(\\d+)_R(\\d+)");

    public static ServerVersion fromString(String versionString) {
        Matcher matcher = VERSION_PATTERN.matcher(versionString);
        if (matcher.matches()) {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));

            String enumName = "v" + major + "_" + minor + "_R" + patch;
            try {
                return ServerVersion.valueOf(enumName);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public boolean isGreaterThan(ServerVersion other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isGreaterThanOrEqualTo(ServerVersion other) {
        return this.ordinal() >= other.ordinal();
    }
}
