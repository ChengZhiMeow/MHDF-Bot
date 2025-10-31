package cn.chengzhiya.mhdfbot.api.group.data;

import com.alibaba.fastjson2.JSONObject;

public record Group(Long groupId, String groupName, int memberCount, int maxMemberCount) {
    public static Group fromJson(JSONObject data) {
        return new Group(
                data.getLong("group_id"),
                data.getString("group_name"),
                data.getIntValue("member_count"),
                data.getIntValue("max_member_count")
        );
    }
}