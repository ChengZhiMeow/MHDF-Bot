package cn.chengzhiya.mhdfbot.api.log;

import lombok.Getter;
import org.apache.logging.log4j.Logger;

public abstract class LoggerManager {
    @Getter
    private static LoggerManager instance;

    /**
     * 获取日志实例
     *
     * @param id 日志的前缀
     * @return 日志实例
     */
    public abstract Logger getLogger(String id);
}
