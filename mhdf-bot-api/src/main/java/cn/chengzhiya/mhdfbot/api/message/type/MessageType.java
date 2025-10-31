package cn.chengzhiya.mhdfbot.api.message.type;

public enum MessageType {
    GROUP,
    PRIVATE,
    OTHER;

    public static MessageType get(String type) {
        return type != null ? switch (type) {
            case "group" -> MessageType.GROUP;
            case "private" -> MessageType.PRIVATE;
            default -> MessageType.OTHER;
        } : MessageType.OTHER;
    }
}
