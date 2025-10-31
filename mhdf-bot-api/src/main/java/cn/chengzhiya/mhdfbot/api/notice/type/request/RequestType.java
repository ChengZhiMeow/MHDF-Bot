package cn.chengzhiya.mhdfbot.api.notice.type.request;

public enum RequestType {
    GROUP,
    FRIEND,
    OTHER;

    public static RequestType get(String type) {
        return type != null ? switch (type) {
            case "group" -> RequestType.GROUP;
            case "friend" -> RequestType.FRIEND;
            default -> RequestType.OTHER;
        } : RequestType.OTHER;
    }
}
