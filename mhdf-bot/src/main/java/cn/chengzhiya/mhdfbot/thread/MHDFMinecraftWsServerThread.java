package cn.chengzhiya.mhdfbot.thread;

public final class MHDFMinecraftWsServerThread extends MHDFScheduledThread {
    private static MHDFMinecraftWsServerThread instance;

    public static MHDFMinecraftWsServerThread getInstance() {
        if (MHDFMinecraftWsServerThread.instance == null)
            MHDFMinecraftWsServerThread.instance = new MHDFMinecraftWsServerThread();
        return MHDFMinecraftWsServerThread.instance;
    }

    public MHDFMinecraftWsServerThread() {
        super("MHDF-Bot Minecraft-Websocket-Server Thread");
    }
}
