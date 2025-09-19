package cn.chengzhiya.mhdfbot.api.enums.notice;

public enum NoticeType {
    GROUP_UPLOAD,
    GROUP_ADMIN,
    GROUP_DECREASE,
    GROUP_INCREASE,
    GROUP_BAN,
    GROUP_CARD,
    GROUP_RECALL,
    FRIEND_RECALL,
    FRIEND_ADD,
    NOTIFY;

    public static NoticeType get(String type) {
        return type != null ? switch (type) {
            case "group_upload" -> NoticeType.GROUP_UPLOAD;
            case "group_admin" -> NoticeType.GROUP_ADMIN;
            case "group_decrease" -> NoticeType.GROUP_DECREASE;
            case "group_increase" -> NoticeType.GROUP_INCREASE;
            case "group_ban" -> NoticeType.GROUP_BAN;
            case "group_card" -> NoticeType.GROUP_CARD;
            case "group_recall" -> NoticeType.GROUP_RECALL;
            case "friend_add" -> NoticeType.FRIEND_ADD;
            case "friend_recall" -> NoticeType.FRIEND_RECALL;
            default -> NoticeType.NOTIFY;
        } : NoticeType.NOTIFY;
    }
}
