package cn.chengzhiya.mhdfbot.scheduler;

import cn.chengzhiya.mhdfbot.api.scheduler.Scheduler;
import cn.chengzhiya.mhdfbot.thread.MHDFScheduledThread;

public final class MHDFScheduler extends MHDFScheduledThread implements Scheduler {
    public MHDFScheduler() {
        super(1, "MHDF-Bot Async-Scheduler Thread");
    }

    @Override
    public void runTask(Runnable runnable) {
        this.execute(runnable);
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        this.schedule(runnable, delay);
    }

    @Override
    public void runTaskTimer(Runnable runnable, long delay, long period) {
        this.schedule(runnable, delay, period);
    }

    @Override
    public void shutdown() {
        this.kill();
    }
}
