package cn.chengzhiya.mhdfbot.api.scheduler;

public interface Scheduler {
    /**
     * 异步执行
     *
     * @param runnable 运行的runnable实例
     */
    void runTask(Runnable runnable);

    /**
     * 异步延迟执行
     *
     * @param runnable 运行的runnable实例
     * @param delay    延迟多少秒运行
     */
    void runTaskLater(Runnable runnable, long delay);

    /**
     * 异步定期执行
     *
     * @param runnable 运行的runnable实例
     * @param delay    延迟多少秒运行
     * @param period   每多少秒执行一次
     */
    void runTaskTimer(Runnable runnable, long delay, long period);

    /**
     * 销毁线程池
     */
    void shutdown();
}
