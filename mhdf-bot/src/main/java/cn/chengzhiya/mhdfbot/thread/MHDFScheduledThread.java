package cn.chengzhiya.mhdfbot.thread;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class MHDFScheduledThread {
    @Getter
    private final String id;
    private final ScheduledExecutorService thread;

    public MHDFScheduledThread(int size, String id) {
        this.id = id;
        this.thread = new ScheduledThreadPoolExecutor(size, new ThreadFactory() {
            private int id = 0;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                id++;
                if (size == 1) return new Thread(r, MHDFScheduledThread.this.id);
                return new Thread(r, MHDFScheduledThread.this.id + " Pool-" + id);
            }
        });
    }

    public MHDFScheduledThread(String id) {
        this(1, id);
    }

    /**
     * 关闭线程池
     */
    public void kill() {
        this.thread.shutdown();
    }

    /**
     * 执行任务
     *
     * @param task 任务实例
     */
    public void execute(Runnable task) {
        this.thread.submit(() -> {
            try {
                task.run();
            } catch (Throwable e) {
                MHDFBot.getLogger().error(e);
            }
        });
    }

    /**
     * 延迟执行任务
     *
     * @param task  任务实例
     * @param delay 延迟时间(单位: 毫秒)
     */
    public void schedule(Runnable task, long delay) {
        this.thread.schedule(() -> {
            try {
                task.run();
            } catch (Throwable e) {
                MHDFBot.getLogger().error(e);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时执行任务
     *
     * @param task   任务实例
     * @param delay  延迟时间(单位: 毫秒)
     * @param period 间隔时间(单位: 毫秒)
     */
    public void schedule(Runnable task, long delay, long period) {
        this.thread.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Throwable e) {
                MHDFBot.getLogger().error(e);
            }
        }, delay, period, TimeUnit.MILLISECONDS);
    }
}
