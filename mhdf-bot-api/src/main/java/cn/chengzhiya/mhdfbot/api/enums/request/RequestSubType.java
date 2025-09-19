package cn.chengzhiya.mhdfbot.api.enums.request;

public enum RequestSubType {
    ADD,
    INVITE,
    OTHER;

    public static RequestSubType get(String subType) {
        return subType != null ? switch (subType) {
            case "add" -> RequestSubType.ADD;
            case "invite" -> RequestSubType.INVITE;
            default -> RequestSubType.OTHER;
        } : RequestSubType.OTHER;
    }
}
