package cn.chengzhiya.mhdfbot.api.group.data;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public record GroupHonors(
        long groupId,
        GroupHonor currentTalkative,
        List<GroupHonor> talkativeList,
        List<GroupHonor> performerList,
        List<GroupHonor> legendList,
        List<GroupHonor> strongNewbieList,
        List<GroupHonor> emotionList
) {
    public static GroupHonors fromJson(JSONObject data) {
        return new GroupHonors(
                data.getLong("group_id"),
                GroupHonor.fromJson(data.getJSONObject("current_talkative")),
                data.getList("talkative_list", GroupHonor.class),
                data.getList("performer_list", GroupHonor.class),
                data.getList("legend_list", GroupHonor.class),
                data.getList("strong_newbie_list", GroupHonor.class),
                data.getList("emotion_list", GroupHonor.class)
        );
    }
}