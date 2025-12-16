package cn.chengzhiya.mhdfbot.lang;

public final class Languages {
    // 插件
    public static final String PLUGIN_LOAD_ERROR_INVALID = "{}不是一个有效的插件!";
    public static final String PLUGIN_LOADING = "插件{}({})加载中";
    public static final String PLUGIN_LOAD_ERROR_THROW_EXCEPTION = "在加载插件 {} 的时候遇到了问题:";
    public static final String PLUGIN_ENABLING = "插件{}({})启用中";
    public static final String PLUGIN_ENABLE_ERROR_THROW_EXCEPTION = "在启用插件 {} 的时候遇到了问题:";
    public static final String PLUGIN_UNLOADING = "插件{}({})卸载中";
    public static final String PLUGIN_UNLOADING_ERROR_THROW_EXCEPTION = "在卸载插件 {} 的时候遇到了问题:";
    // 错误
    public static final String FILE_CORRUPTED = "框架文件损坏,请重新下载后重试!";
    public static final String NOT_FOUND_COMMAND = "找不到命令!";
    public static final String NOT_FOUND_BOT_CONFIG = "找不到机器人配置!";
    public static final String NOT_SUPPORT_BOT_TYPE = "不支持的机器人类型!";
    public static final String NOT_SUPPORT_API = "当前机器人类型不支持该操作!";
    public static final String NOT_FOUND_MESSAGE_ID = "无法获取消息的消息ID,返回数据: {}";
    // WebHook服务端
    public static final String WEBHOOK_GET_FILE_INVALID_PATH_MESSAGE = "该目录无法访问!";
    public static final String WEBHOOK_GET_FILE_INVALID_PATH_LOG = "{}尝试访问{},操作已拦截!";
    public static final String WEBHOOK_START_DONE = "WebHook服务端启动成功,本次启动时长: {}ms";
    // Minecraft-WebSocket服务端
    public static final String MINECRAFT_WEBSOCKET_CONNECT = "客户端{}连接Minecraft-WebSocket服务端!";
    public static final String MINECRAFT_WEBSOCKET_DISCONNECT = "客户端{}断开Minecraft-WebSocket服务端!";
    public static final String MINECRAFT_WEBSOCKET_START_DONE = "Minecraft-WebSocket服务端启动成功(0.0.0.0:{})!";
    // 程序本体
    public static final String START_DONE = "启动成功,本次启动时长: {}ms";
    // 监听器
    public static final String CALL_EVENT_THROW_EXCEPTION = "在处理监听器 {} 的时候遇到了问题:";
    // 官方QQ机器人
    public static final String QQ_BOT_UPDATE_TOKEN_FAILED = "机器人访问密钥更新失败, 错误码: {}({})";
    public static final String QQ_BOT_UPDATE_TOKEN_DONE = "机器人访问密钥更新完成, 新的密钥: {}, 下次更新还需要 {} 秒后!";
    public static final String QQ_BOT_SEND_MESSAGE_WITH_NO_REPLY = "官方机器人模式下消息必须带有回复!";
    public static final String QQ_BOT_SEND_MESSAGE_ERROR = "消息({})发送失败,内容: {}";
    // 消息日志
    public static final String MESSAGE_LOG_PRIVATE = "在私聊收到了一条消息: {}({}): {}";
    public static final String MESSAGE_LOG_GROUP = "在群聊{}收到了一条消息: {}({}): {}";
    public static final String MESSAGE_LOG_WEBHOOK = "收到来自腾讯服务器的事件, 事件: {}, 数据: {}";
    // help命令
    public static final String COMMAND_HELP_DESCRIPTION = "查看命令帮助";
    public static final String COMMAND_HELP_USAGE = "help <页数>";
    // plugins命令
    public static final String COMMAND_PLUGINS_DESCRIPTION = "查看插件列表";

    private Languages() {
    }
}
