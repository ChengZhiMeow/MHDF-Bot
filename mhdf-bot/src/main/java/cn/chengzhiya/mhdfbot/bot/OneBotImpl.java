package cn.chengzhiya.mhdfbot.bot;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.bot.Bot;
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
import lombok.SneakyThrows;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Getter
public final class OneBotImpl implements Bot {
    private OneBotHttpClient httpClient;
    private OneBotWebSocketClient webSocketClient;

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
        this.httpClient = new OneBotHttpClient();
        this.webSocketClient = new OneBotWebSocketClient();

        this.getWebSocketClient().connectServer();
    }

    @Override
    @SneakyThrows
    public void cleanCache() {
        this.getHttpClient().post("clean_cache");
    }

    @Override
    @SneakyThrows
    public void restart(Long delay) {
        JSONObject body = new JSONObject();
        body.put("delay", delay);

        this.getHttpClient().post("set_restart", body.toString());
    }

    @Override
    public void restart() {
        this.restart(0L);
    }

    @Override
    @SneakyThrows
    public Status getStatus() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_status"));

        return new Status(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    @SneakyThrows
    public VersionInfo getVersionInfo() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_version_info"));

        return new VersionInfo(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    @SneakyThrows
    public LoginInfo getLoginInfo() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_login_info"));

        return new LoginInfo(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    @SneakyThrows
    public Boolean ifCanSendRecord() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("if_can_send_record"));

        return Objects.requireNonNull(data).getJSONObject("data").getBoolean("yes");
    }

    @Override
    @SneakyThrows
    public Boolean ifCanSendImage() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("if_can_send_image"));

        return Objects.requireNonNull(data).getJSONObject("data").getBoolean("yes");
    }

    @Override
    @SneakyThrows
    public long getCsrfToken() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_csrf_token"));

        return Objects.requireNonNull(data).getLong("token");
    }

    @Override
    @SneakyThrows
    public List<Friend> getFriendList() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_friend_list"));

        return Objects.requireNonNull(data).getList("data", JSONObject.class).stream()
                .map(Friend::new)
                .toList();
    }

    @Override
    @SneakyThrows
    public List<Group> getGroupList() {
        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_group_list"));

        return Objects.requireNonNull(data).getList("data", JSONObject.class).stream()
                .map(Group::new)
                .toList();
    }

    @Override
    @SneakyThrows
    public AbstractMessageEvent getMsg(Long messageId) {
        JSONObject body = new JSONObject();
        body.put("message_id", messageId);

        JSONObject data = Objects.requireNonNull(JSONObject.parseObject(this.getHttpClient().post("get_msg", body.toString())))
                .getJSONObject("data");

        return switch (data.getString("message_type")) {
            case "group" -> new GroupMessageEvent(body);
            case "private" -> new PrivateMessageEvent(body);
            default -> null;
        };
    }

    @Override
    @SneakyThrows
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

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("send_msg", body.toString()));
        return Objects.requireNonNull(data).getLong("message_id");
    }

    @Override
    public long sendPrivateMsg(Long targetId, String message, boolean autoEscape) {
        return this.sendMsg(MessageType.GROUP, targetId, message, autoEscape);
    }

    @Override
    public long sendPrivateMsg(Long targetId, String message) {
        return this.sendPrivateMsg(targetId, message, false);
    }

    @Override
    public long sendGroupMsg(Long targetId, String message, boolean autoEscape) {
        return this.sendMsg(MessageType.GROUP, targetId, message, autoEscape);
    }

    @Override
    public long sendGroupMsg(Long targetId, String message) {
        return this.sendGroupMsg(targetId, message, false);
    }

    @Override
    @SneakyThrows
    public void deleteMsg(Long messageId) {
        JSONObject body = new JSONObject();
        body.put("message_id", messageId);

        this.getHttpClient().post("delete_msg", body.toString());
    }

    @Override
    @SneakyThrows
    public void sendLike(Long targetId, int times) {
        JSONObject body = new JSONObject();
        body.put("user_id", targetId);
        body.put("times", times);

        this.getHttpClient().post("send_like", body.toString());
    }

    @Override
    @SneakyThrows
    public void groupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("reject_add_request", rejectAddRequest);

        this.getHttpClient().post("set_group_kick", body.toString());
    }

    @Override
    public void groupKick(Long groupId, Long userId) {
        this.groupKick(groupId, userId, false);
    }

    @Override
    @SneakyThrows
    public void setGroupMute(Long groupId, Long userId, Long duration) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("duration", duration);

        this.getHttpClient().post("set_group_ban", body.toString());
    }

    @Override
    public void setGroupMute(Long groupId, Long userId) {
        this.setGroupMute(groupId, userId, 1800L);
    }

    @Override
    public void unsetGroupMute(Long groupId, Long userId) {
        this.setGroupMute(groupId, userId, 0L);
    }

    @Override
    @SneakyThrows
    public void setGroupWholeMute(Long groupId, boolean enable) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("enable", enable);

        this.getHttpClient().post("set_group_whole_ban", body.toString());
    }

    @Override
    public void setGroupWholeMute(Long groupId) {
        this.setGroupWholeMute(groupId, true);
    }

    @Override
    public void unsetGroupWholeMute(Long groupId) {
        this.setGroupWholeMute(groupId, false);
    }

    @Override
    @SneakyThrows
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("enable", enable);

        this.getHttpClient().post("set_group_admin", body.toString());
    }

    @Override
    public void setGroupAdmin(Long groupId, Long userId) {
        this.setGroupAdmin(groupId, userId, true);
    }

    @Override
    public void unsetGroupAdmin(Long groupId, Long userId) {
        this.setGroupAdmin(groupId, userId, false);
    }

    @Override
    @SneakyThrows
    public void setGroupCard(Long groupId, Long userId, String card) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("card", card);

        this.getHttpClient().post("set_group_card", body.toString());
    }

    @Override
    public void unsetGroupCard(Long groupId, Long userId) {
        this.setGroupCard(groupId, userId, null);
    }

    @Override
    @SneakyThrows
    public void setGroupName(Long groupId, String name) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("group_name", name);

        this.getHttpClient().post("set_group_name", body.toString());
    }

    @Override
    @SneakyThrows
    public void leaveGroup(Long groupId, boolean dismiss) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("is_dismiss", dismiss);

        this.getHttpClient().post("set_group_leave", body.toString());
    }

    @Override
    public void leaveGroup(Long groupId) {
        this.leaveGroup(groupId, false);
    }

    @Override
    public void dismissGroup(Long groupId) {
        this.leaveGroup(groupId, true);
    }

    @Override
    @SneakyThrows
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle, Long duration) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("special_title", specialTitle);
        body.put("duration", duration);

        this.getHttpClient().post("set_group_special_title", body.toString());
    }

    @Override
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        this.setGroupSpecialTitle(groupId, userId, specialTitle, -1L);
    }

    @Override
    public void unsetGroupSpecialTitle(Long groupId, Long userId) {
        this.setGroupSpecialTitle(groupId, userId, null);
    }

    @Override
    @SneakyThrows
    public void handleFriendAddRequest(String flag, boolean approve, String remark) {
        JSONObject body = new JSONObject();
        body.put("flag", flag);
        body.put("approve", approve);
        body.put("remark", remark);

        this.getHttpClient().post("set_friend_add_request", body.toString());
    }

    @Override
    public void handleFriendAddRequest(String flag, boolean approve) {
        this.handleFriendAddRequest(flag, approve, null);
    }

    @Override
    public void acceptFriendAddRequest(String flag) {
        this.handleFriendAddRequest(flag, true);
    }

    @Override
    public void rejectFriendAddRequest(String flag) {
        this.handleFriendAddRequest(flag, false);
    }

    @Override
    @SneakyThrows
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve, String reason) {
        JSONObject body = new JSONObject();
        body.put("flag", flag);
        body.put("type", type.toString().toLowerCase(Locale.ROOT));
        body.put("approve", approve);
        body.put("reason", reason);

        this.getHttpClient().post("set_group_add_request", body.toString());
    }

    @Override
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve) {
        this.handleGroupAddRequest(flag, type, approve, null);
    }

    @Override
    public void acceptGroupAddRequest(String flag, RequestSubType type) {
        this.handleGroupAddRequest(flag, type, true);
    }

    @Override
    public void rejectGroupAddRequest(String flag, RequestSubType type) {
        this.handleGroupAddRequest(flag, type, false);
    }

    @Override
    @SneakyThrows
    public Stranger getStrangerInfo(Long userId, boolean cache) {
        JSONObject body = new JSONObject();
        body.put("user_id", userId);
        body.put("no_cache", !cache);

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_stranger_info", body.toString()));

        return new Stranger(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public Stranger getStrangerInfo(Long userId) {
        return this.getStrangerInfo(userId, true);
    }

    @Override
    @SneakyThrows
    public Member getGroupMemberInfo(Long groupId, Long userId, boolean cache) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("user_id", userId);
        body.put("no_cache", !cache);

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_group_member_info", body.toString()));

        return new Member(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public Member getGroupMemberInfo(Long groupId, Long userId) {
        return this.getGroupMemberInfo(groupId, userId, true);
    }

    @Override
    @SneakyThrows
    public List<Member> getGroupMemberList(Long groupId, boolean cache) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("no_cache", !cache);

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_group_member_list", body.toString()));

        return Objects.requireNonNull(data).getList("data", JSONObject.class).stream()
                .map(Member::new)
                .toList();
    }

    @Override
    public List<Member> getGroupMemberList(Long groupId) {
        return this.getGroupMemberList(groupId, true);
    }

    @Override
    @SneakyThrows
    public GroupHonor getGroupHonorInfo(Long groupId, HonorType type) {
        JSONObject body = new JSONObject();
        body.put("group_id", groupId);
        body.put("type", type.toString().toLowerCase(Locale.ROOT));

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_group_honor_info", body.toString()));

        return new GroupHonor(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public GroupHonor getGroupHonorInfo(Long groupId) {
        return this.getGroupHonorInfo(groupId, HonorType.ALL);
    }

    @Override
    @SneakyThrows
    public String getCookies(String domain) {
        JSONObject body = new JSONObject();
        body.put("domain", domain);

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_cookies", body.toString()));

        return Objects.requireNonNull(data).getString("cookies");
    }

    @Override
    @SneakyThrows
    public Record getRecord(String file, RecordFormat format) {
        JSONObject body = new JSONObject();
        body.put("file", file);
        body.put("format", format.toString());

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_record", body.toString()));

        return new Record(Objects.requireNonNull(data).getJSONObject("data"));
    }

    @Override
    public Record getRecord(String file) {
        return this.getRecord(file, RecordFormat.WAV);
    }

    @Override
    @SneakyThrows
    public File getImage(String file) {
        JSONObject body = new JSONObject();
        body.put("file", file);

        JSONObject data = JSONObject.parseObject(this.getHttpClient().post("get_image", body.toString()));

        return new java.io.File(Objects.requireNonNull(data).getJSONObject("data").getString("file"));
    }
}
