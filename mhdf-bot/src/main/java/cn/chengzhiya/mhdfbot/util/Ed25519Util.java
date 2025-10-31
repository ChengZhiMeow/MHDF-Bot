package cn.chengzhiya.mhdfbot.util;

import java.util.HexFormat;

public final class Ed25519Util {
    /**
     * 处理种子
     *
     * @param seed 种子值
     * @return 处理后的种子
     */
    public static byte[] handleSeed(String seed) {
        StringBuilder seedBuilder = new StringBuilder(seed);
        if (seedBuilder.length() < 32) {
            seedBuilder.append(seedBuilder, 0, 32 - seedBuilder.length());
        }
        if (seedBuilder.length() > 32) {
            seedBuilder = new StringBuilder(seedBuilder.substring(0, 32));
        }
        return seedBuilder.toString().getBytes();
    }

    /**
     * 验证签名
     *
     * @param signature 签名
     * @return 结果
     */
    public static boolean verifySignature(String signature) {
        if (signature == null || signature.isEmpty()) {
            return false;
        }

        HexFormat hexFormat = HexFormat.of();
        byte[] sig = hexFormat.parseHex(signature);

        if (sig.length != 64) {
            return false;
        }

        int lastByte = sig[63] & 0xFF;
        return (lastByte & 0xE0) == 0;
    }
}
