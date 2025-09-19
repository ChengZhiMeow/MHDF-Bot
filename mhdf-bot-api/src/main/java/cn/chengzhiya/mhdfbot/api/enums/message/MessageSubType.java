package cn.chengzhiya.mhdfbot.api.enums.message;

public enum MessageSubType {
    FRIEND,
    GROUP,
    NORMAL,
    ANONYMOUS,
    NOTICE,
    OTHER;

    public static MessageSubType get(String subType) {
        return subType != null ? switch (subType) {
            case "friend" -> MessageSubType.FRIEND;
            case "group" -> MessageSubType.GROUP;
            case "normal" -> MessageSubType.NORMAL;
            case "anonymous" -> MessageSubType.ANONYMOUS;
            case "notice" -> MessageSubType.NOTICE;
            default -> MessageSubType.OTHER;
        } : MessageSubType.OTHER;
    }
}
