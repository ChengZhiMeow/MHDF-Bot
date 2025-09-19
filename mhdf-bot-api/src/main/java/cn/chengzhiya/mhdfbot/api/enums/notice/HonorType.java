package cn.chengzhiya.mhdfbot.api.enums.notice;

public enum HonorType {
    TALKATIVE,
    PERFORMER,
    LEGEND,
    STRONG_NEWBIE,
    EMOTION,
    ALL,
    OTHER;

    public static HonorType get(String subType) {
        return subType != null ? switch (subType) {
            case "talkative" -> HonorType.TALKATIVE;
            case "performer" -> HonorType.PERFORMER;
            case "legend" -> HonorType.LEGEND;
            case "strong_newbie" -> HonorType.STRONG_NEWBIE;
            case "emotion" -> HonorType.EMOTION;
            case "all" -> HonorType.ALL;
            default -> HonorType.OTHER;
        } : HonorType.OTHER;
    }
}
