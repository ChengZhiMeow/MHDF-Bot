package cn.chengzhiya.mhdfbot.bot.qqbot;

import cn.chengzhimeow.ccyaml.configuration.ConfigurationSection;
import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.bot.data.BotLoginInfo;
import cn.chengzhiya.mhdfbot.api.bot.data.BotStatus;
import cn.chengzhiya.mhdfbot.api.bot.data.BotVersionInfo;
import cn.chengzhiya.mhdfbot.api.bot.type.OpenIdType;
import cn.chengzhiya.mhdfbot.api.event.message.AbstractMessageEvent;
import cn.chengzhiya.mhdfbot.api.file.data.MediaInfo;
import cn.chengzhiya.mhdfbot.api.file.type.MediaType;
import cn.chengzhiya.mhdfbot.api.group.data.Group;
import cn.chengzhiya.mhdfbot.api.group.data.GroupHonors;
import cn.chengzhiya.mhdfbot.api.manager.OpenIdCacheManager;
import cn.chengzhiya.mhdfbot.api.message.data.RecordInfo;
import cn.chengzhiya.mhdfbot.api.message.type.MessageType;
import cn.chengzhiya.mhdfbot.api.message.type.RecordFormat;
import cn.chengzhiya.mhdfbot.api.notice.type.HonorType;
import cn.chengzhiya.mhdfbot.api.notice.type.request.RequestSubType;
import cn.chengzhiya.mhdfbot.api.user.data.Friend;
import cn.chengzhiya.mhdfbot.api.user.data.Member;
import cn.chengzhiya.mhdfbot.api.user.data.Stranger;
import cn.chengzhiya.mhdfbot.bot.MHDFAbstractBot;
import cn.chengzhiya.mhdfbot.lang.Languages;
import cn.chengzhiya.mhdfhttpframework.server.entity.SSLConfig;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MHDFQqBot extends MHDFAbstractBot {
    public static final String ACCESS_TOKEN_API_URL = "https://bots.qq.com/app/getAppAccessToken";
    public static final String OPEN_API_URL = "https://api.sgroup.qq.com";

    @Getter
    private final BotVersionInfo versionInfo = new BotVersionInfo("腾讯官方机器人", "1.0.0", "11");
    @Getter
    private final BotStatus status = new BotStatus(true, true);
    @Getter
    private final ConfigurationSection botConfig;
    private final long botQq;

    private final MHDFQqBotHttpClient httpClient;
    private final MHDFQqBotHttpServer httpServer;

    @Setter
    private String botName;

    public MHDFQqBot() {
        this.botConfig = Main.getConfigManager().getData().getConfigurationSection("bot_settings.qq_bot");
        if (this.botConfig == null) throw new RuntimeException(Languages.NOT_FOUND_BOT_CONFIG);

        this.botQq = this.botConfig.getLong("qq");

        SSLConfig sslConfig = new SSLConfig();
        ConfigurationSection config = this.botConfig.getConfigurationSection("web_hook.ssl");
        if (config != null) {
            sslConfig.setEnable(config.getBoolean("enable"));
            sslConfig.setAlias(config.getString("alias"));
            sslConfig.setFile(config.getString("file"));
            sslConfig.setKey(config.getString("key"));
        }

        this.httpClient = new MHDFQqBotHttpClient();
        this.httpServer = new MHDFQqBotHttpServer(
                this.botConfig.getInt("web_hook.port"),
                sslConfig
        );
    }

    /**
     * 更新访问密钥
     */
    @SneakyThrows
    private void updateAccessToken() {
        JSONObject body = new JSONObject();
        body.put("appId", this.botConfig.getString("app_id"));
        body.put("clientSecret", this.botConfig.getString("secret"));

        JSONObject data = JSONObject.parseObject(this.httpClient.post(MHDFQqBot.ACCESS_TOKEN_API_URL, body.toString()));

        Integer code = data.getInteger("code");
        if (code != null) {
            MHDFBot.getLogger().error(Languages.QQ_BOT_UPDATE_TOKEN_FAILED,
                    code,
                    data.getString("message")
            );
            return;
        }

        String accessToken = "QQBot " + data.getString("access_token");
        int updateTime = data.getInteger("expires_in") - 30;

        this.httpClient.getHeaderHashMap().put("Authorization", accessToken);
        MHDFBot.getLogger().info(Languages.QQ_BOT_UPDATE_TOKEN_DONE,
                accessToken,
                updateTime
        );

        super.getScheduler().runTaskLater(this::updateAccessToken, updateTime * 1000L);
    }

    /**
     * 更新机器人名称
     */
    @SneakyThrows
    private void updateBotName() {
        JSONObject data = JSONObject.parseObject(this.httpClient.get(MHDFQqBot.OPEN_API_URL + "/users/@me"));
        this.setBotName(data.getString("username"));
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

            mediaUrl = Objects.requireNonNull(this.botConfig.getString("web_hook.default_file_url_format"))
                    .replace("{name}", fileName);
        }

        JSONObject body = new JSONObject();
        body.put("file_type", type.ordinal());
        body.put("url", mediaUrl);
        body.put("srv_send_msg", false);

        String targetType = openIdType == OpenIdType.GROUP ? "groups" : "users";
        String url = MHDFQqBot.OPEN_API_URL + "/v2/" + targetType + "/" + openId + "/files";

        JSONObject data = JSONObject.parseObject(this.httpClient.post(url, body.toString()));
        return MediaInfo.fromJson(data);
    }

    @Override
    @SneakyThrows
    public void init() {
        this.updateAccessToken();
        this.updateBotName();

        long startTime = System.currentTimeMillis();
        this.httpServer.start();
        MHDFBot.getLogger().info(Languages.WEBHOOK_START_DONE, System.currentTimeMillis() - startTime);
    }

    @Override
    public void cleanCache() {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void restart(Long delay) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void restart() {
        this.restart(0L);
    }

    @Override
    public BotLoginInfo getLoginInfo() {
        return new BotLoginInfo(this.botQq, this.botName);
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
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public List<Friend> getFriendList() {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public List<Group> getGroupList() {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public AbstractMessageEvent getMsg(Long messageId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    @SneakyThrows
    public long sendMsg(MessageType messageType, Long targetId, String message, boolean autoEscape) {
        // 获取消息ID
        String messageId;
        {
            Pattern pattern = Pattern.compile("\\[CQ:reply,id=(\\d+)]");
            Matcher matcher = pattern.matcher(message);
            if (!matcher.find()) {
                MHDFBot.getLogger().error(Languages.QQ_BOT_SEND_MESSAGE_WITH_NO_REPLY);
                return -1;
            }
            messageId = matcher.group(1);
        }

        String targetType = messageType == MessageType.GROUP ? "groups" : "users";
        OpenIdType openIdType = messageType == MessageType.GROUP ? OpenIdType.GROUP : OpenIdType.USER;
        String openId = OpenIdCacheManager.getInstance().getData(openIdType, Math.toIntExact(targetId));

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

                    media = this.getMediaInfo(mediaType, openIdType, openId, file);
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

        String url = MHDFQqBot.OPEN_API_URL + "/v2/" + targetType + "/" + openId + "/messages";

        JSONObject body = new JSONObject();
        body.put("content", allowText ? message : null);
        body.put("msg_type", hasMedia ? 7 : 0);
        body.put("media", hasMedia ? media.toJsonObject() : null);
        body.put("msg_id", OpenIdCacheManager.getInstance().getData(OpenIdType.MESSAGE, Integer.parseInt(messageId)));
        body.put("msg_seq", 1);

        JSONObject data = JSONObject.parseObject(this.httpClient.post(url, body.toString()));
        if (data == null) {
            MHDFBot.getLogger().error(Languages.QQ_BOT_SEND_MESSAGE_ERROR, messageType.name(), message);
            return -1;
        }

        return OpenIdCacheManager.getInstance().addData(OpenIdType.MESSAGE, data.getString("id"));
    }

    @Override
    public long sendPrivateMsg(Long targetId, String message, boolean autoEscape) {
        return this.sendMsg(MessageType.PRIVATE, targetId, message, autoEscape);
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
        return this.sendMsg(MessageType.GROUP, targetId, message, false);
    }

    @Override
    public void deleteMsg(Long messageId) {
        throw new RuntimeException("开发中!");
    }

    @Override
    public void sendLike(Long targetId, int times) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void groupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void groupKick(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupMute(Long groupId, Long userId, Long duration) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupMute(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void unsetGroupMute(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupWholeMute(Long groupId, boolean enable) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupWholeMute(Long groupId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void unsetGroupWholeMute(Long groupId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupAdmin(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void unsetGroupAdmin(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupCard(Long groupId, Long userId, String card) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void unsetGroupCard(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupName(Long groupId, String name) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void leaveGroup(Long groupId, boolean dismiss) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void leaveGroup(Long groupId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void dismissGroup(Long groupId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle, Long duration) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void unsetGroupSpecialTitle(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void handleFriendAddRequest(String flag, boolean approve, String remark) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void handleFriendAddRequest(String flag, boolean approve) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void acceptFriendAddRequest(String flag) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void rejectFriendAddRequest(String flag) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve, String reason) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void handleGroupAddRequest(String flag, RequestSubType type, boolean approve) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void acceptGroupAddRequest(String flag, RequestSubType type) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public void rejectGroupAddRequest(String flag, RequestSubType type) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public Stranger getStrangerInfo(Long userId, boolean cache) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public Stranger getStrangerInfo(Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public Member getGroupMemberInfo(Long groupId, Long userId, boolean cache) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public Member getGroupMemberInfo(Long groupId, Long userId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public List<Member> getGroupMemberList(Long groupId, boolean cache) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public List<Member> getGroupMemberList(Long groupId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public GroupHonors getGroupHonorInfo(Long groupId, HonorType type) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public GroupHonors getGroupHonorInfo(Long groupId) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public String getCookies(String domain) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public RecordInfo getRecord(String file, RecordFormat format) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public RecordInfo getRecord(String file) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }

    @Override
    public File getImage(String file) {
        throw new RuntimeException(Languages.NOT_SUPPORT_API);
    }
}
