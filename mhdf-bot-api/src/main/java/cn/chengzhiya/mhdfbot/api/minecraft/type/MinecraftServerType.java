package cn.chengzhiya.mhdfbot.api.minecraft.type;

public enum MinecraftServerType {
    BUNGEE,
    BUKKIT,
    OTHER;

    public static MinecraftServerType get(String subType) {
        return subType != null ? switch (subType) {
            case "bungee" -> MinecraftServerType.BUNGEE;
            case "bukkit" -> MinecraftServerType.BUKKIT;
            default -> MinecraftServerType.OTHER;
        } : MinecraftServerType.OTHER;
    }
}
