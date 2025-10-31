package cn.chengzhiya.mhdfbot.bot.qqbot.controller;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.http.HttpUtil;
import cn.chengzhiya.mhdfbot.api.http.JsonHttpData;
import cn.chengzhiya.mhdfbot.util.Ed25519Util;
import cn.chengzhiya.mhdfhttpframework.api.enums.RequestTypes;
import cn.chengzhiya.mhdfhttpframework.server.annotation.BodyData;
import cn.chengzhiya.mhdfhttpframework.server.annotation.Priority;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestPath;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestType;
import com.alibaba.fastjson2.JSONObject;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HexFormat;

@RequestPath("/")
public final class SignController {
    @Priority(-1)
    @RequestPath("/default")
    @RequestType(RequestTypes.POST)
    public static boolean postSign(HttpServletRequest request, HttpServletResponse response,
                                   @BodyData("d") JSONObject d
    ) {
        String eventTs = d.getString("event_ts");
        String plainToken = d.getString("plain_token");
        String signature = request.getHeader("x-signature-ed25519");
        if (eventTs == null || plainToken == null || !Ed25519Util.verifySignature(signature)) {
            HttpUtil.returnJsonHttpData(response, JsonHttpData.noAuth);
            return true;
        }

        String msg = eventTs + plainToken;

        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(Ed25519Util.handleSeed(MHDFBot.getBot().getBotConfig().getString("secret")));
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privateKey);
        signer.update(msg.getBytes(), 0, msg.getBytes().length);
        byte[] signatureBytes = signer.generateSignature();

        HexFormat hexFormat = HexFormat.of();
        String msgSignature = hexFormat.formatHex(signatureBytes);

        JSONObject data = new JSONObject();
        data.put("plain_token", plainToken);
        data.put("signature", msgSignature);

        HttpUtil.returnJsonHttpData(response, data);
        return true;
    }
}
