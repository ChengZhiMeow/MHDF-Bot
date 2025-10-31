package cn.chengzhiya.mhdfbot.thread;

public final class MHDFBotServiceThread extends MHDFScheduledThread {
    private static MHDFBotServiceThread instance;

    public static MHDFBotServiceThread getInstance() {
        if (MHDFBotServiceThread.instance == null) MHDFBotServiceThread.instance = new MHDFBotServiceThread();
        return MHDFBotServiceThread.instance;
    }

    public MHDFBotServiceThread() {
        super("MHDF-Bot Bot-Service Thread");
    }
}
