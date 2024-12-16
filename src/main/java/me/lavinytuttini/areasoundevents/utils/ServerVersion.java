package me.lavinytuttini.areasoundevents.utils;

public enum ServerVersion {
    UNKNOWN,
    v1_17_1,
    v1_18,
    v1_18_1,
    v1_18_2,
    v1_19,
    v1_19_1,
    v1_19_2,
    v1_19_3,
    v1_19_4,
    v1_20,
    v1_20_1,
    v1_20_2,
    v1_20_4,
    v1_20_5,
    v1_20_6,
    v1_21,
    v1_21_1,
    v1_21_3;

    public static ServerVersion fromString(String bukkitVersion) {
        if (bukkitVersion == null || bukkitVersion.isEmpty()) {
            return UNKNOWN;
        }

        try {
            String[] versionParts = bukkitVersion.split("-")[0].split("\\.");
            if (versionParts.length < 2) {
                return UNKNOWN;
            }

            String formattedVersion = "v" + versionParts[0] + "_" + versionParts[1];
            if (versionParts.length > 2) {
                formattedVersion += "_" + versionParts[2];
            }

            return ServerVersion.valueOf(formattedVersion);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    public boolean isGreaterThanOrEqualTo(ServerVersion other) {
        return this.ordinal() >= other.ordinal();
    }
}
