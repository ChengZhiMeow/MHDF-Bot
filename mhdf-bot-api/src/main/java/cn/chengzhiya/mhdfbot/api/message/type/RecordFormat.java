package cn.chengzhiya.mhdfbot.api.message.type;

public enum RecordFormat {
    MP3,
    AMR,
    WMA,
    M4A,
    SPX,
    OGG,
    WAV,
    FLAC,
    OTHER;

    public static RecordFormat get(String subType) {
        return subType != null ? switch (subType) {
            case "mp3" -> RecordFormat.MP3;
            case "amr" -> RecordFormat.AMR;
            case "wma" -> RecordFormat.WMA;
            case "m4a" -> RecordFormat.M4A;
            case "spx" -> RecordFormat.SPX;
            case "ogg" -> RecordFormat.OGG;
            case "wav" -> RecordFormat.WAV;
            case "flac" -> RecordFormat.FLAC;
            default -> RecordFormat.OTHER;
        } : RecordFormat.OTHER;
    }
}
