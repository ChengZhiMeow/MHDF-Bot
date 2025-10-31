package cn.chengzhiya.mhdfbot.api.runnable;

import cn.chengzhiya.mhdfbot.api.MHDFBot;

@SuppressWarnings("unused")
public abstract class MHDFBotRunnable implements Runnable {
    public void runTask() {
        MHDFBot.getScheduler().runTask(this);
    }

    public void runTaskLater(long delay) {
        MHDFBot.getScheduler().runTaskLater(this, delay);
    }

    public void runTaskTimer(long delay, long period) {
        MHDFBot.getScheduler().runTaskTimer(this, delay, period);
    }
}
