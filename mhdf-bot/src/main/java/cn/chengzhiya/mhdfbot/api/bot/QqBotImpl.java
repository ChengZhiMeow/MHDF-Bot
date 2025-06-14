package cn.chengzhiya.mhdfbot.api.bot;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.entity.bot.LoginInfo;
import cn.chengzhiya.mhdfbot.api.entity.bot.Status;
import cn.chengzhiya.mhdfbot.api.entity.bot.VersionInfo;
import cn.chengzhiya.mhdfbot.api.entity.config.YamlConfiguration;
import cn.chengzhiya.mhdfbot.api.entity.file.MediaInfo;
import cn.chengzhiya.mhdfbot.api.entity.group.Group;
import cn.chengzhiya.mhdfbot.api.entity.group.GroupHonor;
import cn.chengzhiya.mhdfbot.api.entity.message.Record;
import cn.chengzhiya.mhdfbot.api.entity.user.Friend;
import cn.chengzhiya.mhdfbot.api.entity.user.Member;
import cn.chengzhiya.mhdfbot.api.entity.user.Stranger;
import cn.chengzhiya.mhdfbot.api.enums.bot.OpenIdType;
import cn.chengzhiya.mhdfbot.api.enums.file.MediaType;
import cn.chengzhiya.mhdfbot.api.enums.message.MessageType;
import cn.chengzhiya.mhdfbot.api.enums.message.RecordFormat;
import cn.chengzhiya.mhdfbot.api.enums.notice.HonorType;
import cn.chengzhiya.mhdfbot.api.enums.request.RequestSubType;
import cn.chengzhiya.mhdfbot.api.event.message.AbstractMessageEvent;
import cn.chengzhiya.mhdfbot.api.http.entity.SSLConfig;
import cn.chengzhiya.mhdfbot.api.util.OpenIdCacheUtil;
import cn.chengzhiya.mhdfbot.qqbot.QqBotHttpClient;
import cn.chengzhiya.mhdfbot.qqbot.QqBotHttpServer;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public final class QqBotImpl implements Bot {
    private final String accessTokenUrl = "https://bots.qq.com/app/getAppAccessToken";
    private final String openApiUrl = "https://api.sgroup.qq.com";
    private final QqBotHttpClient httpClient = new QqBotHttpClient();
    private final QqBotHttpServer httpServer = new QqBotHttpServer(
            getBotConfig().getInt("webHook.port"),
            new SSLConfig(getBotConfig().getConfigurationSection("webHook.ssl"))
    );
    @Setter
    private String botName;

    @Override
    public YamlConfiguration getBotConfig() {
        YamlConfiguration config = Main.getConfigManager().getConfig().getConfigurationSection("botSettings.qqBot");
        if (config == null) {
            throw new NullPointerException("机器人配置错误!");
        }

        return config;
    }

    /**
     * 更新访问密钥
     */
    private void updateAccessToken() {
        JSONObject body = new JSONObject();
        body.put("appId", getBotConfig().getString("appId"));
        body.put("clientSecret", getBotConfig().getString("secret"));

        JSONObject data = JSONObject.parseObject(getHttpClient().post(getAccessTokenUrl(), body));

        Integer code = data.getInteger("code");
        if (code != null) {
            MHDFBot.getLogger().info("机器人访问密钥更新失败, 错误码: {}({})",
                    code,
                    data.getString("message")
            );
            return;
        }

        String accessToken = "QQBot " + data.getString("access_token");
        int updateTime = data.getInteger("expires_in") - 30;

        getHttpClient().setAccessToken(accessToken);
        MHDFBot.getLogger().info("机器人访问密钥更新完成, 新的密钥: {}, 下次更新还需要 {} 秒后!",
                accessToken,
                updateTime
        );

        MHDFBot.getScheduler().runTaskAsynchronouslyLater(this::updateAccessToken, updateTime);
    }

    /**
     * 更新机器人名称
     */
    private void updateBotName() {
        JSONObject data = JSONObject.parseObject(getHttpClient().get(getOpenApiUrl() + "/users/@me"));
        setBotName(data.getString("username"));
    }

    /**
     * 获取媒体资源实例
     *
     * @param type       资源类型
     * @param openIdType 目标ID类型
     * @param openId     目标ID
     * @param mediaUrl   资源URL
     * @return 媒体资源实例
     */
    @SneakyThrows
    private MediaInfo getMediaInfo(MediaType type, OpenIdType openIdType, String openId, String mediaUrl) {
        if (!mediaUrl.startsWith("http")) {
            File file = new File(mediaUrl);
            String fileName = UUID.randomUUID() + mediaUrl.substring(mediaUrl.lastIndexOf("."));
            File outFile = new File("files", fileName);
            Files.copy(file.toPath(), outFile.toPath());

            mediaUrl = getBotConfig().getString("webHook.defaultFileFormat")
                    .replace("{name}", fileName);
        }
        JSONObject body = new JSONObject();
        body.put("file_type", type.ordinal() + 1);
        body.put("url", mediaUrl);
        body.put("srv_send_msg", false);

        String targetType = openIdType == OpenIdType.GROUP ? "groups" : "users";
        String url = getOpenApiUrl() + "/v2/" + targetType + "/" + openId + "/files";

        JSONObject data = JSONObject.parseObject(getHttpClient().post(url, body));
        return new MediaInfo(data);
    }

    @Override
    public void init() {
        updateAccessToken();
        updateBotName();
        getHttpServer().start();
    }

    @Override
    public void cleanCache() {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void restart(Long delay) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void restart() {
        restart(0L);
    }

    @Override
    public Status getStatus() {
        JSONObject data = new JSONObject();
        data.put("online", true);
        data.put("good", true);

        return new Status(data);
    }

    @Override
    public VersionInfo getVersionInfo() {
        JSONObject data = new JSONObject();
        data.put("app_name", "腾讯官方机器人");
        data.put("app_version", "1.0.0");
        data.put("protocol_version", "11");

        return new VersionInfo(data);
    }

    @Override
    public LoginInfo getLoginInfo() {
        JSONObject data = new JSONObject();
        data.put("user_id", getBotConfig().getLong("qq"));
        data.put("nickname", getBotName());
        return new LoginInfo(data);
    }

    @Override
    public Boolean ifCanSendRecord() {
        return true;
    }

    @Override
    public Boolean ifCanSendImage() {
        return true;
    }

    @Override
    public long getCsrfToken() {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public List<Friend> getFriendList() {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public List<Group> getGroupList() {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public AbstractMessageEvent getMsg(Long messageId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public long sendMsg(MessageType messageType, Long targetId, String message, boolean autoEscape) {
        // 获取消息ID
        String messageId;
        {
            Pattern pattern = Pattern.compile("\\[CQ:reply,id=(\\d+)]");
            Matcher matcher = pattern.matcher(message);
            if (!matcher.find()) {
                MHDFBot.getLogger().error("官方机器人模式下消息必须带有回复!");
                return 0;
            }
            messageId = matcher.group(1);
        }

        String targetType = messageType == MessageType.GROUP ? "groups" : "users";
        OpenIdType openIdType = messageType == MessageType.GROUP ? OpenIdType.GROUP : OpenIdType.USER;
        String openId = OpenIdCacheUtil.getData(openIdType, Math.toIntExact(targetId));

        // 处理资源信息
        boolean hasMedia = false;
        boolean allowText = true;
        MediaInfo media = null;
        {
            for (MediaType mediaType : MediaType.values()) {
                Pattern pattern = Pattern.compile("\\[CQ:" + mediaType.name().toLowerCase() + ",file=([^,\\]]+)");
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()) {
                    String file = matcher.group(1);

                    media = getMediaInfo(mediaType, openIdType, openId, file);
                    hasMedia = true;
                    if (mediaType != MediaType.IMAGE) {
                        allowText = false;
                    }
                }
            }
        }

        {
            Pattern pattern = Pattern.compile("\\[CQ:.*]");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                message = message.replace(matcher.group(), "");
            }
        }

        String url = getOpenApiUrl() + "/v2/" + targetType + "/" + openId + "/messages";

        JSONObject body = new JSONObject();
        body.put("content", allowText ? message : null);
        body.put("msg_type", hasMedia ? 7 : 0);
        body.put("media", hasMedia ? media.toJsonObject() : null);
        body.put("msg_id", OpenIdCacheUtil.getData(OpenIdType.MESSAGE, Integer.parseInt(messageId)));
        body.put("msg_seq", 1);

        JSONObject data = JSONObject.parseObject(getHttpClient().post(url, body));
        if (data == null) {
            MHDFBot.getLogger().info("消息({})发送失败,内容: {}",
                    messageType.name(),
                    message
            );
            return -999;
        }

        return OpenIdCacheUtil.addData(OpenIdType.MESSAGE, data.getString("id"));
    }

    @Override
    public long sendPrivateMsg(Long targetId, String message, boolean autoEscape) {
        return sendMsg(MessageType.PRIVATE, targetId, message, autoEscape);
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
        return sendMsg(MessageType.GROUP, targetId, message, false);
    }

    @Override
    public void deleteMsg(Long messageId) {
        throw new RuntimeException("开发中!");
    }

    @Override
    public void sendLike(Long targetId, int times) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void groupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void groupKick(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupMute(Long groupId, Long userId, Long duration) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupMute(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void unsetGroupMute(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupWholeMute(Long groupId, boolean enable) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupWholeMute(Long groupId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void unsetGroupWholeMute(Long groupId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupAdmin(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void unsetGroupAdmin(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupCard(Long groupId, Long userId, String card) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void unsetGroupCard(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupName(Long groupId, String name) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void leaveGroup(Long groupId, boolean dismiss) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void leaveGroup(Long groupId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void dismissGroup(Long groupId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle, Long duration) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void unsetGroupSpecialTitle(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void handleFriendAddRequest(String flag, boolean approve, String remark) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void handleFriendAddRequest(String flag, boolean approve) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void acceptFriendAddRequest(String flag) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void rejectFriendAddRequest(String flag) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve, String reason) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void acceptGroupAddRequest(String flag, RequestSubType type) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public void rejectGroupAddRequest(String flag, RequestSubType type) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public Stranger getStrangerInfo(Long userId, boolean cache) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public Stranger getStrangerInfo(Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public Member getGroupMemberInfo(Long groupId, Long userId, boolean cache) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public Member getGroupMemberInfo(Long groupId, Long userId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public List<Member> getGroupMemberList(Long groupId, boolean cache) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public List<Member> getGroupMemberList(Long groupId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public GroupHonor getGroupHonorInfo(Long groupId, HonorType type) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public GroupHonor getGroupHonorInfo(Long groupId) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public String getCookies(String domain) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public Record getRecord(String file, RecordFormat format) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public Record getRecord(String file) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }

    @Override
    public File getImage(String file) {
        throw new RuntimeException("官方机器人不支持该操作!");
    }
}
