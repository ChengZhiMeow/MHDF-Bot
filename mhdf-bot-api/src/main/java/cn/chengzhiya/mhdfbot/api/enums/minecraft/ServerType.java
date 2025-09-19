package cn.chengzhiya.mhdfbot.api.enums.minecraft;

public enum ServerType {
    BUNGEE,
    BUKKIT,
    OTHER;

    public static ServerType get(String subType) {
        return subType != null ? switch (subType) {
            case "bungee" -> ServerType.BUNGEE;
            case "bukkit" -> ServerType.BUKKIT;
            default -> ServerType.OTHER;
        } : ServerType.OTHER;
    }
}
