package cn.chengzhiya.mhdfbot.api.notice.type;

public enum GroupIncreaseSubType {
    APPROVE,
    INVITE,
    OTHER;

    public static GroupIncreaseSubType get(String subType) {
        return subType != null ? switch (subType) {
            case "approve" -> GroupIncreaseSubType.APPROVE;
            case "invite" -> GroupIncreaseSubType.INVITE;
            default -> GroupIncreaseSubType.OTHER;
        } : GroupIncreaseSubType.OTHER;
    }
}
