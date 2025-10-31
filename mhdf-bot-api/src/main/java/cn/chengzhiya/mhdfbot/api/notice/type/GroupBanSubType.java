package cn.chengzhiya.mhdfbot.api.notice.type;

public enum GroupBanSubType {
    BAN,
    LIFT_BAN,
    OTHER;

    public static GroupBanSubType get(String subType) {
        return subType != null ? switch (subType) {
            case "ban" -> GroupBanSubType.BAN;
            case "lift_ban" -> GroupBanSubType.LIFT_BAN;
            default -> GroupBanSubType.OTHER;
        } : GroupBanSubType.OTHER;
    }
}
