package cn.chengzhiya.mhdfbot.api;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.bot.Bot;
import cn.chengzhiya.mhdfbot.api.bot.OneBotImpl;
import cn.chengzhiya.mhdfbot.api.bot.QqBotImpl;
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
import cn.chengzhiya.mhdfbot.api.enums.bot.BotType;
import cn.chengzhiya.mhdfbot.api.enums.message.MessageType;
import cn.chengzhiya.mhdfbot.api.enums.message.RecordFormat;
import cn.chengzhiya.mhdfbot.api.enums.notice.HonorType;
import cn.chengzhiya.mhdfbot.api.enums.request.RequestSubType;
import cn.chengzhiya.mhdfbot.api.event.message.AbstractMessageEvent;
import cn.chengzhiya.mhdfbot.api.manager.CommandManager;
import cn.chengzhiya.mhdfbot.api.manager.ListenerManager;
import cn.chengzhiya.mhdfbot.api.manager.PluginManager;
import cn.chengzhiya.mhdfbot.api.manager.SchedulerManager;
import cn.chengzhiya.mhdfbot.minecraft.MinecraftWebSocketServer;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.io.File;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public final class MHDFBot {
    @Getter
    private static final Logger logger = getLogger("MHDF-Bot");
    @Getter
    private static final BotType botType;
    @Getter
    private static final Bot bot;
    @Getter
    private static final PluginManager pluginManager = new PluginManager();
    @Getter
    private static final CommandManager commandManager = new CommandManager();
    @Getter
    private static final ListenerManager listenerManager = new ListenerManager();
    @Getter
    private static final MinecraftWebSocketServer minecraftWebSocketServer = new MinecraftWebSocketServer();

    static {
        YamlConfiguration botConfig = Main.getConfigManager().getConfig().getConfigurationSection("botSettings");
        if (botConfig == null) {
            throw new RuntimeException("机器人配置错误!");
        }

        botType = BotType.valueOf(botConfig.getString("type").toUpperCase(Locale.ROOT));
        switch (botType) {
            case ONEBOT -> bot = new OneBotImpl();
            case QQBOT -> bot = new QqBotImpl();
            default -> throw new RuntimeException("不支持的机器人类型!");
        }
    }

    /**
     * 获取调度器实例
     *
     * @return 调度器实例
     */
    public static SchedulerManager getScheduler() {
        return new SchedulerManager();
    }

    /**
     * 获取日志实例
     *
     * @param prefix 日志前缀
     * @return 日志实例
     */
    public static Logger getLogger(String prefix) {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder()
                .setConfigurationName(prefix);

        AppenderComponentBuilder appender = builder.newAppender(prefix, "Console")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", "[%d{HH:mm:ss} %p] [" + prefix + "] %msg%n")
                );

        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(org.apache.logging.log4j.Level.DEBUG)
                .add(builder.newAppenderRef(prefix));

        builder.add(appender).add(rootLogger);
        Configurator.initialize(builder.build());

        return LogManager.getLogger(prefix);
    }

    public static void init() {
        getBot().init();
    }

    public static void cleanCache() {
        getBot().cleanCache();
    }

    public static void restart(Long delay) {
        getBot().restart(delay);
    }

    public static void restart() {
        getBot().restart();
    }

    public static Status getStatus() {
        return getBot().getStatus();
    }

    public static VersionInfo getVersionInfo() {
        return getBot().getVersionInfo();
    }

    public static LoginInfo getLoginInfo() {
        return getBot().getLoginInfo();
    }

    public static Boolean ifCanSendRecord() {
        return getBot().ifCanSendRecord();
    }

    public static Boolean ifCanSendImage() {
        return getBot().ifCanSendImage();
    }

    public static Long getCsrfToken() {
        return getBot().getCsrfToken();
    }

    public static List<Friend> getFriendList() {
        return getBot().getFriendList();
    }

    public static List<Group> getGroupList() {
        return getBot().getGroupList();
    }

    public static AbstractMessageEvent getMsg(Long messageId) {
        return getBot().getMsg(messageId);
    }

    public static Long sendMsg(MessageType messageType, Long targetId, String message, boolean autoEscape) {
        return getBot().sendMsg(messageType, targetId, message, autoEscape);
    }

    public static Long sendPrivateMsg(Long targetId, String message, boolean autoEscape) {
        return getBot().sendPrivateMsg(targetId, message, autoEscape);
    }

    public static Long sendPrivateMsg(Long targetId, String message) {
        return getBot().sendPrivateMsg(targetId, message);
    }

    public static Long sendGroupMsg(Long targetId, String message, boolean autoEscape) {
        return getBot().sendGroupMsg(targetId, message, autoEscape);
    }

    public static Long sendGroupMsg(Long targetId, String message) {
        return getBot().sendGroupMsg(targetId, message);
    }

    public static void deleteMsg(Long messageId) {
        getBot().deleteMsg(messageId);
    }

    public static void sendLike(Long targetId, int times) {
        getBot().sendLike(targetId, times);
    }

    public static void groupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        getBot().groupKick(groupId, userId, rejectAddRequest);
    }

    public static void groupKick(Long groupId, Long userId) {
        getBot().groupKick(groupId, userId);
    }

    public static void setGroupMute(Long groupId, Long userId, Long duration) {
        getBot().setGroupMute(groupId, userId, duration);
    }

    public static void setGroupMute(Long groupId, Long userId) {
        getBot().setGroupMute(groupId, userId);
    }

    public static void unsetGroupMute(Long groupId, Long userId) {
        getBot().unsetGroupMute(groupId, userId);
    }

    public static void setGroupWholeMute(Long groupId, boolean enable) {
        getBot().setGroupWholeMute(groupId, enable);
    }

    public static void setGroupWholeMute(Long groupId) {
        getBot().setGroupWholeMute(groupId);
    }

    public static void unsetGroupWholeMute(Long groupId) {
        getBot().unsetGroupWholeMute(groupId);
    }

    public static void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        getBot().setGroupAdmin(groupId, userId, enable);
    }

    public static void setGroupAdmin(Long groupId, Long userId) {
        getBot().setGroupAdmin(groupId, userId);
    }

    public static void unsetGroupAdmin(Long groupId, Long userId) {
        getBot().unsetGroupAdmin(groupId, userId);
    }

    public static void setGroupCard(Long groupId, Long userId, String card) {
        getBot().setGroupCard(groupId, userId, card);
    }

    public static void unsetGroupCard(Long groupId, Long userId) {
        getBot().unsetGroupCard(groupId, userId);
    }

    public static void setGroupName(Long groupId, String name) {
        getBot().setGroupName(groupId, name);
    }

    public static void leaveGroup(Long groupId, boolean dismiss) {
        getBot().leaveGroup(groupId, dismiss);
    }

    public static void leaveGroup(Long groupId) {
        getBot().leaveGroup(groupId);
    }

    public static void dismissGroup(Long groupId) {
        getBot().dismissGroup(groupId);
    }

    public static void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle, Long duration) {
        getBot().setGroupSpecialTitle(groupId, userId, specialTitle, duration);
    }

    public static void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        getBot().setGroupSpecialTitle(groupId, userId, specialTitle);
    }

    public static void unsetGroupSpecialTitle(Long groupId, Long userId) {
        getBot().unsetGroupSpecialTitle(groupId, userId);
    }

    public static void handleFriendAddRequest(String flag, boolean approve, String remark) {
        getBot().handleFriendAddRequest(flag, approve, remark);
    }

    public static void handleFriendAddRequest(String flag, boolean approve) {
        getBot().handleFriendAddRequest(flag, approve);
    }

    public static void acceptFriendAddRequest(String flag) {
        getBot().acceptFriendAddRequest(flag);
    }

    public static void rejectFriendAddRequest(String flag) {
        getBot().rejectFriendAddRequest(flag);
    }

    public static void handleGroupAddRequest(String flag, RequestSubType type, boolean approve, String reason) {
        getBot().handleGroupAddRequest(flag, type, approve, reason);
    }

    public static void handleGroupAddRequest(String flag, RequestSubType type, boolean approve) {
        getBot().handleGroupAddRequest(flag, type, approve);
    }

    public static void acceptGroupAddRequest(String flag, RequestSubType type) {
        getBot().acceptGroupAddRequest(flag, type);
    }

    public static void rejectGroupAddRequest(String flag, RequestSubType type) {
        getBot().rejectGroupAddRequest(flag, type);
    }

    public static Stranger getStrangerInfo(Long userId, boolean cache) {
        return getBot().getStrangerInfo(userId, cache);
    }

    public static Stranger getStrangerInfo(Long userId) {
        return getBot().getStrangerInfo(userId);
    }

    public static Member getGroupMemberInfo(Long groupId, Long userId, boolean cache) {
        return getBot().getGroupMemberInfo(groupId, userId, cache);
    }

    public static Member getGroupMemberInfo(Long groupId, Long userId) {
        return getBot().getGroupMemberInfo(groupId, userId);
    }

    public static List<Member> getGroupMemberList(Long groupId, boolean cache) {
        return getBot().getGroupMemberList(groupId, cache);
    }

    public static List<Member> getGroupMemberList(Long groupId) {
        return getBot().getGroupMemberList(groupId);
    }

    public static GroupHonor getGroupHonorInfo(Long groupId, HonorType type) {
        return getBot().getGroupHonorInfo(groupId, type);
    }

    public static GroupHonor getGroupHonorInfo(Long groupId) {
        return getBot().getGroupHonorInfo(groupId);
    }

    public static String getCookies(String domain) {
        return getBot().getCookies(domain);
    }

    public static Record getRecord(String file, RecordFormat format) {
        return getBot().getRecord(file, format);
    }

    public static Record getRecord(String file) {
        return getBot().getRecord(file);
    }

    public static File getImage(String file) {
        return getBot().getImage(file);
    }
}
