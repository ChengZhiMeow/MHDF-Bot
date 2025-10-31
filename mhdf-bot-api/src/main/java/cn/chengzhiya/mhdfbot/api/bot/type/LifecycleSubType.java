package cn.chengzhiya.mhdfbot.api.bot.type;

public enum LifecycleSubType {
    CONNECT,
    ENABLE,
    DISABLE,
    OTHER;

    public static LifecycleSubType get(String subType) {
        return subType != null ? switch (subType) {
            case "connect" -> LifecycleSubType.CONNECT;
            case "enable" -> LifecycleSubType.ENABLE;
            case "disable" -> LifecycleSubType.DISABLE;
            default -> LifecycleSubType.OTHER;
        } : LifecycleSubType.OTHER;
    }
}
