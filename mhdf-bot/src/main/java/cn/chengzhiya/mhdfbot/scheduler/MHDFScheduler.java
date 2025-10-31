package cn.chengzhiya.mhdfbot.scheduler;

import cn.chengzhiya.mhdfbot.api.scheduler.Scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MHDFScheduler implements Scheduler {
    private final ScheduledExecutorService asyncScheduled = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void runTask(Runnable runnable) {
        this.asyncScheduled.submit(runnable);
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        this.asyncScheduled.schedule(runnable, delay, TimeUnit.SECONDS);
    }

    @Override
    public void runTaskTimer(Runnable runnable, long delay, long period) {
        this.asyncScheduled.scheduleAtFixedRate(runnable, delay, period, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        this.asyncScheduled.shutdown();
    }
}
