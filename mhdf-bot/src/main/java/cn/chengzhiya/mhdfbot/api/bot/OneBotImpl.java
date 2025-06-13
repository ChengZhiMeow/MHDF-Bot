package cn.chengzhiya.mhdfbot.api.bot;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.entity.bot.LoginInfo;
import cn.chengzhiya.mhdfbot.api.entity.bot.Status;
import cn.chengzhiya.mhdfbot.api.entity.bot.VersionInfo;
import cn.chengzhiya.mhdfbot.api.entity.config.YamlConfiguration;
import cn.chengzhiya.mhdfbot.api.entity.group.Group;
import cn.chengzhiya.mhdfbot.api.entity.group.GroupHonor;
import cn.chengzhiya.mhdfbot.api.entity.message.Record;
import cn.chengzhiya.mhdfbot.api.entity.user.Friend;
import cn.chengzhiya.mhdfbot.api.entity.user.Member;
import cn.chengzhiya.mhdfbot.api.entity.user.Stranger;
import cn.chengzhiya.mhdfbot.api.enums.message.MessageType;
import cn.chengzhiya.mhdfbot.api.enums.message.RecordFormat;
import cn.chengzhiya.mhdfbot.api.enums.notice.HonorType;
import cn.chengzhiya.mhdfbot.api.enums.request.RequestSubType;
import cn.chengzhiya.mhdfbot.api.event.message.AbstractMessageEvent;
import cn.chengzhiya.mhdfbot.api.event.message.GroupMessageEvent;
import cn.chengzhiya.mhdfbot.api.event.message.PrivateMessageEvent;
import cn.chengzhiya.mhdfbot.onebot.OneBotHttpClient;
import cn.chengzhiya.mhdfbot.onebot.OneBotWebSocketClient;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Getter
public final class OneBotImpl implements Bot {
    private final OneBotHttpClient httpClient = new OneBotHttpClient();
    private final OneBotWebSocketClient webSocketClient = new OneBotWebSocketClient();

    @Override
    public YamlConfiguration getBotConfig() {
        YamlConfiguration config = Main.getConfigManager().getConfig().getConfigurationSection("botSettings.ontBot");
        if (config == null) {
            throw new NullPointerException("机器人配置错误!");
        }

        return config;
    }

    @Override
    public void init() {
        getWebSocketClient().connectServer();
    }

    @Override
    public void cleanCache() {
        getHttpClient().post("clean_cache");
    }

    @Override
    public void restart(Long delay) {
        JSONObject body = new JSONObject();
        body.put("delay", delay);

        getHttpClient().post("set_restart", body);
    }

    @Override
    public void restart() {
        restart(0L);
    }

    @Override
    public Status getStatus() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_status"));

        return new Status(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public VersionInfo getVersionInfo() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_version_info"));

        return new VersionInfo(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public LoginInfo getLoginInfo() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_login_info"));

        return new LoginInfo(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public Boolean ifCanSendRecord() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("if_can_send_record"));

        return Objects.requireNonNull(data).getJSONObject("data").getBoolean("yes");
    }

    @Override
    public Boolean ifCanSendImage() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("if_can_send_image"));

        return Objects.requireNonNull(data).getJSONObject("data").getBoolean("yes");
    }

    @Override
    public long getCsrfToken() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_csrf_token"));

        return Objects.requireNonNull(data).getLong("token");
    }

    @Override
    public List<Friend> getFriendList() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_friend_list"));

        return Objects.requireNonNull(data).getList("data", JSONObject.class).stream()
                .map(Friend::new)
                .toList();
    }

    @Override
    public List<Group> getGroupList() {
        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_group_list"));

        return Objects.requireNonNull(data).getList("data", JSONObject.class).stream()
                .map(Group::new)
                .toList();
    }

    @Override
    public AbstractMessageEvent getMsg(Long messageId) {
        JSONObject body = new JSONObject();
        body.put("message_id", messageId);

        JSONObject data = Objects.requireNonNull(JSONObject.parseObject(getHttpClient().post("get_msg", body)))
                .getJSONObject("data");

        return switch (data.getString("message_type")) {
            case "group" -> new GroupMessageEvent(body);
            case "private" -> new PrivateMessageEvent(body);
            default -> null;
        };
    }

    @Override
    public long sendMsg(MessageType messageType, Long targetId, String message, boolean autoEscape) {
        JSONObject body = new JSONObject();
        if (messageType == MessageType.GROUP) {
            body.put("message_type", "group");
            body.put("group_id", targetId);
        }
        if (messageType == MessageType.PRIVATE) {
            body.put("message_type", "private");
            body.put("user_id", targetId);
        }
        body.put("message", message);
        body.put("auto_escape", autoEscape);

        JSONObject data = JSONObject.parseObject(getHttpClient().post("send_msg", body));
        return Objects.requireNonNull(data).getLong("message_id");
    }

    @Override
    public long sendPrivateMsg(Long targetId, String message, boolean autoEscape) {
        return sendMsg(MessageType.GROUP, targetId, message, autoEscape);
    }

    @Override
    public long sendPrivateMsg(Long targetId, String message) {
        return sendPrivateMsg(targetId, message, false);
    }

    @Override
    public long sendGroupMsg(Long targetId, String message, boolean autoEscape) {
        return sendMsg(MessageType.GROUP, targetId, message, autoEscape);
    }

    @Override
    public long sendGroupMsg(Long targetId, String message) {
        return sendGroupMsg(targetId, message, false);
    }

    @Override
    public void deleteMsg(Long messageId) {
        JSONObject body = new JSONObject();
        body.put("message_id", messageId);

        getHttpClient().post("delete_msg", body);
    }

    @Override
    public void sendLike(Long targetId, int times) {
        JSONObject body = new JSONObject();
        body.put("user_id", targetId);
        body.put("times", times);

        getHttpClient().post("send_like", body);
    }

    @Override
    public void groupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("reject_add_request", rejectAddRequest);

        getHttpClient().post("set_group_kick", body);
    }

    @Override
    public void groupKick(Long groupId, Long userId) {
        groupKick(groupId, userId, false);
    }

    @Override
    public void setGroupMute(Long groupId, Long userId, Long duration) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("duration", duration);

        getHttpClient().post("set_group_ban", body);
    }

    @Override
    public void setGroupMute(Long groupId, Long userId) {
        setGroupMute(groupId, userId, 1800L);
    }

    @Override
    public void unsetGroupMute(Long groupId, Long userId) {
        setGroupMute(groupId, userId, 0L);
    }

    @Override
    public void setGroupWholeMute(Long groupId, boolean enable) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("enable", enable);

        getHttpClient().post("set_group_whole_ban", body);
    }

    @Override
    public void setGroupWholeMute(Long groupId) {
        setGroupWholeMute(groupId, true);
    }

    @Override
    public void unsetGroupWholeMute(Long groupId) {
        setGroupWholeMute(groupId, false);
    }

    @Override
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("enable", enable);

        getHttpClient().post("set_group_admin", body);
    }

    @Override
    public void setGroupAdmin(Long groupId, Long userId) {
        setGroupAdmin(groupId, userId, true);
    }

    @Override
    public void unsetGroupAdmin(Long groupId, Long userId) {
        setGroupAdmin(groupId, userId, false);
    }

    @Override
    public void setGroupCard(Long groupId, Long userId, String card) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("card", card);

        getHttpClient().post("set_group_card", body);
    }

    @Override
    public void unsetGroupCard(Long groupId, Long userId) {
        setGroupCard(groupId, userId, null);
    }

    @Override
    public void setGroupName(Long groupId, String name) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("group_name", name);

        getHttpClient().post("set_group_name", body);
    }

    @Override
    public void leaveGroup(Long groupId, boolean dismiss) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("is_dismiss", dismiss);

        getHttpClient().post("set_group_leave", body);
    }

    @Override
    public void leaveGroup(Long groupId) {
        leaveGroup(groupId, false);
    }

    @Override
    public void dismissGroup(Long groupId) {
        leaveGroup(groupId, true);
    }

    @Override
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle, Long duration) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("special_title", specialTitle);
        body.put("duration", duration);

        getHttpClient().post("set_group_special_title", body);
    }

    @Override
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        setGroupSpecialTitle(groupId, userId, specialTitle, -1L);
    }

    @Override
    public void unsetGroupSpecialTitle(Long groupId, Long userId) {
        setGroupSpecialTitle(groupId, userId, null);
    }

    @Override
    public void handleFriendAddRequest(String flag, boolean approve, String remark) {
        JSONObject body = new JSONObject();
        body.put("flag", flag);
        body.put("approve", approve);
        body.put("remark", remark);

        getHttpClient().post("set_friend_add_request", body);
    }

    @Override
    public void handleFriendAddRequest(String flag, boolean approve) {
        handleFriendAddRequest(flag, approve, null);
    }

    @Override
    public void acceptFriendAddRequest(String flag) {
        handleFriendAddRequest(flag, true);
    }

    @Override
    public void rejectFriendAddRequest(String flag) {
        handleFriendAddRequest(flag, false);
    }

    @Override
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve, String reason) {
        JSONObject body = new JSONObject();
        body.put("flag", flag);
        body.put("type", type.toString().toLowerCase(Locale.ROOT));
        body.put("approve", approve);
        body.put("reason", reason);

        getHttpClient().post("set_group_add_request", body);
    }

    @Override
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve) {
        handleGroupAddRequest(flag, type, approve, null);
    }

    @Override
    public void acceptGroupAddRequest(String flag, RequestSubType type) {
        handleGroupAddRequest(flag, type, true);
    }

    @Override
    public void rejectGroupAddRequest(String flag, RequestSubType type) {
        handleGroupAddRequest(flag, type, false);
    }

    @Override
    public Stranger getStrangerInfo(Long userId, boolean cache) {
        JSONObject body = new JSONObject();
        body.put("user_id", userId);
        body.put("no_cache", !cache);

        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_stranger_info", body));

        return new Stranger(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public Stranger getStrangerInfo(Long userId) {
        return getStrangerInfo(userId, true);
    }

    @Override
    public Member getGroupMemberInfo(Long groupId, Long userId, boolean cache) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("no_cache", !cache);

        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_group_member_info", body));

        return new Member(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public Member getGroupMemberInfo(Long groupId, Long userId) {
        return getGroupMemberInfo(groupId, userId, true);
    }

    @Override
    public List<Member> getGroupMemberList(Long groupId, boolean cache) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("no_cache", !cache);

        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_group_member_list", body));

        return Objects.requireNonNull(data).getList("data", JSONObject.class).stream()
                .map(Member::new)
                .toList();
    }

    @Override
    public List<Member> getGroupMemberList(Long groupId) {
        return getGroupMemberList(groupId, true);
    }

    @Override
    public GroupHonor getGroupHonorInfo(Long groupId, HonorType type) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("type", type.toString().toLowerCase(Locale.ROOT));

        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_group_honor_info", body));

        return new GroupHonor(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public GroupHonor getGroupHonorInfo(Long groupId) {
        return getGroupHonorInfo(groupId, HonorType.ALL);
    }

    @Override
    public String getCookies(String domain) {
        JSONObject body = new JSONObject();
        body.put("domain", domain);

        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_cookies", body));

        return Objects.requireNonNull(data).getString("cookies");
    }

    @Override
    public Record getRecord(String file, RecordFormat format) {
        JSONObject body = new JSONObject();
        body.put("file", file);
        body.put("format", format.toString());

        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_record", body));

        return new Record(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public Record getRecord(String file) {
        return getRecord(file, RecordFormat.WAV);
    }

    @Override
    public File getImage(String file) {
        JSONObject body = new JSONObject();
        body.put("file", file);

        JSONObject data = JSONObject.parseObject(getHttpClient().post("get_image", body));

        return new java.io.File(Objects.requireNonNull(data).getJSONObject("data").getString("file"));
    }
}
