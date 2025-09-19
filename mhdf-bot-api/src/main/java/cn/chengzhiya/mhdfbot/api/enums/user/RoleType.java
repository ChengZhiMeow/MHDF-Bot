package cn.chengzhiya.mhdfbot.api.enums.user;

public enum RoleType {
    OWNER,
    ADMIN,
    MEMBER;

    public static RoleType get(String type) {
        return type != null ? switch (type) {
            case "owner" -> RoleType.OWNER;
            case "admin" -> RoleType.ADMIN;
            default -> RoleType.MEMBER;
        } : RoleType.MEMBER;
    }
}
