package cn.chengzhiya.mhdfbot.api.notice.type;

public enum GroupAdminSubType {
    SET,
    UNSET,
    OTHER;

    public static GroupAdminSubType get(String subType) {
        return subType != null ? switch (subType) {
            case "set" -> GroupAdminSubType.SET;
            case "unset" -> GroupAdminSubType.UNSET;
            default -> GroupAdminSubType.OTHER;
        } : GroupAdminSubType.OTHER;
    }
}
