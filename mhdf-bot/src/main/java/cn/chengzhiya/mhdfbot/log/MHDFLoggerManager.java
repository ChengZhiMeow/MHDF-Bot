package cn.chengzhiya.mhdfbot.log;

import cn.chengzhiya.mhdfbot.api.log.LoggerManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MHDFLoggerManager extends LoggerManager {
    private final Map<String, Logger> loggerMap = new HashMap<>();
    private final List<String> registeredLoggerIds = new ArrayList<>();

    public void registerLogger(String id) {
        if (this.registeredLoggerIds.contains(id)) return;
        this.registeredLoggerIds.add(id);
    }

    public void initLogger() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setConfigurationName("MHDF-Bot");

        for (String id : this.registeredLoggerIds) {
            AppenderComponentBuilder appenderBuilder = builder.newAppender(id + "-Console", "Console")
                    .addAttribute("target", "SYSTEM_OUT")
                    .add(builder.newLayout("PatternLayout")
                            .addAttribute("pattern", "[%d{HH:mm:ss} %p] [" + id + "] %msg%n")
                    );
            builder.add(appenderBuilder);

            builder.add(builder.newLogger(id, Level.DEBUG)
                    .add(builder.newAppenderRef(id + "-Console"))
                    .addAttribute("additivity", false));
        }

        builder.add(builder.newRootLogger(Level.INFO)
                .add(builder.newAppenderRef("StdOut")));
        builder.add(builder.newAppender("StdOut", "Console")
                .addAttribute("target", "SYSTEM_OUT")
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", "[%d{HH:mm:ss} %p] [ROOT] %msg%n")));
        Configurator.reconfigure(builder.build());

        this.loggerMap.clear();
        for (String id : this.registeredLoggerIds) {
            this.loggerMap.put(id, LogManager.getLogger(id));
        }
    }

    @Override
    public Logger getLogger(String id) {
        Logger logger = this.loggerMap.get(id);
        if (logger == null) {
            return LogManager.getRootLogger();
        }
        return logger;
    }
}
