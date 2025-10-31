package cn.chengzhiya.mhdfbot.api.user.type;

public enum SexType {
    MALE,
    FEMALE,
    UNKNOWN;

    public static SexType get(String type) {
        return type != null ? switch (type) {
            case "male" -> SexType.MALE;
            case "female" -> SexType.FEMALE;
            default -> SexType.UNKNOWN;
        } : SexType.UNKNOWN;
    }
}
