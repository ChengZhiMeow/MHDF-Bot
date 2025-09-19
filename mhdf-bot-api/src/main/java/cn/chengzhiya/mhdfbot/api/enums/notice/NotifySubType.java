package cn.chengzhiya.mhdfbot.api.enums.notice;


public enum NotifySubType {
    POKE,
    LUCKY_KING,
    HONOR,
    INPUT_STATUS,
    OTHER;

    public static NotifySubType get(String subType) {
        return subType != null ? switch (subType) {
            case "poke" -> NotifySubType.POKE;
            case "lucky_king" -> NotifySubType.LUCKY_KING;
            case "honor" -> NotifySubType.HONOR;
            case "input_status" -> NotifySubType.INPUT_STATUS;
            default -> NotifySubType.OTHER;
        } : NotifySubType.OTHER;
    }
}
