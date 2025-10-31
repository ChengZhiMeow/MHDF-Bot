package cn.chengzhiya.mhdfbot.api.notice.type;

public enum GroupDecreaseSubType {
    LEAVE,
    KICK,
    KICK_ME,
    OTHER;

    public static GroupDecreaseSubType get(String subType) {
        return subType != null ? switch (subType) {
            case "leave" -> GroupDecreaseSubType.LEAVE;
            case "kick" -> GroupDecreaseSubType.KICK;
            case "kick_me" -> GroupDecreaseSubType.KICK_ME;
            default -> GroupDecreaseSubType.OTHER;
        } : GroupDecreaseSubType.OTHER;
    }
}

