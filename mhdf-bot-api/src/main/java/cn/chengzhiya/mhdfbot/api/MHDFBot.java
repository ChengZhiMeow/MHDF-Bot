package cn.chengzhiya.mhdfbot.api;

import cn.chengzhiya.mhdfbot.api.bot.Bot;
import cn.chengzhiya.mhdfbot.api.entity.bot.LoginInfo;
import cn.chengzhiya.mhdfbot.api.entity.bot.Status;
import cn.chengzhiya.mhdfbot.api.entity.bot.VersionInfo;
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
import lombok.Getter;
import lombok.Setter;
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

@SuppressWarnings("unused")
public final class MHDFBot {
    @Getter
    private static final Logger logger = MHDFBot.getLogger("MHDF-Bot");
    @Getter
    private static final PluginManager pluginManager = new PluginManager();
    @Getter
    private static final CommandManager commandManager = new CommandManager();
    @Getter
    private static final ListenerManager listenerManager = new ListenerManager();
    @Getter
    @Setter
    private static BotType botType;
    @Getter
    @Setter
    private static Bot bot;

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
        MHDFBot.getBot().init();
    }

    public static void cleanCache() {
        MHDFBot.getBot().cleanCache();
    }

    public static void restart(Long delay) {
        MHDFBot.getBot().restart(delay);
    }

    public static void restart() {
        MHDFBot.getBot().restart();
    }

    public static Status getStatus() {
        return MHDFBot.getBot().getStatus();
    }

    public static VersionInfo getVersionInfo() {
        return MHDFBot.getBot().getVersionInfo();
    }

    public static LoginInfo getLoginInfo() {
        return MHDFBot.getBot().getLoginInfo();
    }

    public static Boolean ifCanSendRecord() {
        return MHDFBot.getBot().ifCanSendRecord();
    }

    public static Boolean ifCanSendImage() {
        return MHDFBot.getBot().ifCanSendImage();
    }

    public static Long getCsrfToken() {
        return MHDFBot.getBot().getCsrfToken();
    }

    public static List<Friend> getFriendList() {
        return MHDFBot.getBot().getFriendList();
    }

    public static List<Group> getGroupList() {
        return MHDFBot.getBot().getGroupList();
    }

    public static AbstractMessageEvent getMsg(Long messageId) {
        return MHDFBot.getBot().getMsg(messageId);
    }

    public static Long sendMsg(MessageType messageType, Long targetId, String message, boolean autoEscape) {
        return MHDFBot.getBot().sendMsg(messageType, targetId, message, autoEscape);
    }

    public static Long sendPrivateMsg(Long targetId, String message, boolean autoEscape) {
        return MHDFBot.getBot().sendPrivateMsg(targetId, message, autoEscape);
    }

    public static Long sendPrivateMsg(Long targetId, String message) {
        return MHDFBot.getBot().sendPrivateMsg(targetId, message);
    }

    public static Long sendGroupMsg(Long targetId, String message, boolean autoEscape) {
        return MHDFBot.getBot().sendGroupMsg(targetId, message, autoEscape);
    }

    public static Long sendGroupMsg(Long targetId, String message) {
        return MHDFBot.getBot().sendGroupMsg(targetId, message);
    }

    public static void deleteMsg(Long messageId) {
        MHDFBot.getBot().deleteMsg(messageId);
    }

    public static void sendLike(Long targetId, int times) {
        MHDFBot.getBot().sendLike(targetId, times);
    }

    public static void groupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        MHDFBot.getBot().groupKick(groupId, userId, rejectAddRequest);
    }

    public static void groupKick(Long groupId, Long userId) {
        MHDFBot.getBot().groupKick(groupId, userId);
    }

    public static void setGroupMute(Long groupId, Long userId, Long duration) {
        MHDFBot.getBot().setGroupMute(groupId, userId, duration);
    }

    public static void setGroupMute(Long groupId, Long userId) {
        MHDFBot.getBot().setGroupMute(groupId, userId);
    }

    public static void unsetGroupMute(Long groupId, Long userId) {
        MHDFBot.getBot().unsetGroupMute(groupId, userId);
    }

    public static void setGroupWholeMute(Long groupId, boolean enable) {
        MHDFBot.getBot().setGroupWholeMute(groupId, enable);
    }

    public static void setGroupWholeMute(Long groupId) {
        MHDFBot.getBot().setGroupWholeMute(groupId);
    }

    public static void unsetGroupWholeMute(Long groupId) {
        MHDFBot.getBot().unsetGroupWholeMute(groupId);
    }

    public static void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        MHDFBot.getBot().setGroupAdmin(groupId, userId, enable);
    }

    public static void setGroupAdmin(Long groupId, Long userId) {
        MHDFBot.getBot().setGroupAdmin(groupId, userId);
    }

    public static void unsetGroupAdmin(Long groupId, Long userId) {
        MHDFBot.getBot().unsetGroupAdmin(groupId, userId);
    }

    public static void setGroupCard(Long groupId, Long userId, String card) {
        MHDFBot.getBot().setGroupCard(groupId, userId, card);
    }

    public static void unsetGroupCard(Long groupId, Long userId) {
        MHDFBot.getBot().unsetGroupCard(groupId, userId);
    }

    public static void setGroupName(Long groupId, String name) {
        MHDFBot.getBot().setGroupName(groupId, name);
    }

    public static void leaveGroup(Long groupId, boolean dismiss) {
        MHDFBot.getBot().leaveGroup(groupId, dismiss);
    }

    public static void leaveGroup(Long groupId) {
        MHDFBot.getBot().leaveGroup(groupId);
    }

    public static void dismissGroup(Long groupId) {
        MHDFBot.getBot().dismissGroup(groupId);
    }

    public static void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle, Long duration) {
        MHDFBot.getBot().setGroupSpecialTitle(groupId, userId, specialTitle, duration);
    }

    public static void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        MHDFBot.getBot().setGroupSpecialTitle(groupId, userId, specialTitle);
    }

    public static void unsetGroupSpecialTitle(Long groupId, Long userId) {
        MHDFBot.getBot().unsetGroupSpecialTitle(groupId, userId);
    }

    public static void handleFriendAddRequest(String flag, boolean approve, String remark) {
        MHDFBot.getBot().handleFriendAddRequest(flag, approve, remark);
    }

    public static void handleFriendAddRequest(String flag, boolean approve) {
        MHDFBot.getBot().handleFriendAddRequest(flag, approve);
    }

    public static void acceptFriendAddRequest(String flag) {
        MHDFBot.getBot().acceptFriendAddRequest(flag);
    }

    public static void rejectFriendAddRequest(String flag) {
        MHDFBot.getBot().rejectFriendAddRequest(flag);
    }

    public static void handleGroupAddRequest(String flag, RequestSubType type, boolean approve, String reason) {
        MHDFBot.getBot().handleGroupAddRequest(flag, type, approve, reason);
    }

    public static void handleGroupAddRequest(String flag, RequestSubType type, boolean approve) {
        MHDFBot.getBot().handleGroupAddRequest(flag, type, approve);
    }

    public static void acceptGroupAddRequest(String flag, RequestSubType type) {
        MHDFBot.getBot().acceptGroupAddRequest(flag, type);
    }

    public static void rejectGroupAddRequest(String flag, RequestSubType type) {
        MHDFBot.getBot().rejectGroupAddRequest(flag, type);
    }

    public static Stranger getStrangerInfo(Long userId, boolean cache) {
        return MHDFBot.getBot().getStrangerInfo(userId, cache);
    }

    public static Stranger getStrangerInfo(Long userId) {
        return MHDFBot.getBot().getStrangerInfo(userId);
    }

    public static Member getGroupMemberInfo(Long groupId, Long userId, boolean cache) {
        return MHDFBot.getBot().getGroupMemberInfo(groupId, userId, cache);
    }

    public static Member getGroupMemberInfo(Long groupId, Long userId) {
        return MHDFBot.getBot().getGroupMemberInfo(groupId, userId);
    }

    public static List<Member> getGroupMemberList(Long groupId, boolean cache) {
        return MHDFBot.getBot().getGroupMemberList(groupId, cache);
    }

    public static List<Member> getGroupMemberList(Long groupId) {
        return MHDFBot.getBot().getGroupMemberList(groupId);
    }

    public static GroupHonor getGroupHonorInfo(Long groupId, HonorType type) {
        return MHDFBot.getBot().getGroupHonorInfo(groupId, type);
    }

    public static GroupHonor getGroupHonorInfo(Long groupId) {
        return MHDFBot.getBot().getGroupHonorInfo(groupId);
    }

    public static String getCookies(String domain) {
        return MHDFBot.getBot().getCookies(domain);
    }

    public static Record getRecord(String file, RecordFormat format) {
        return MHDFBot.getBot().getRecord(file, format);
    }

    public static Record getRecord(String file) {
        return MHDFBot.getBot().getRecord(file);
    }

    public static File getImage(String file) {
        return MHDFBot.getBot().getImage(file);
    }
}
