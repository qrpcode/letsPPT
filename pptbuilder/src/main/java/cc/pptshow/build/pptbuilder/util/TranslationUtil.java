package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.vo.BaiduVo;
import cc.pptshow.build.pptbuilder.domain.vo.TransResult;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class TranslationUtil {

    private static final String APP_ID = "20210206000691977";
    private static final String APP_KEY = "aFPq9ypcAnaAzOQLFVbF";

    public static String cn2En(String query) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String salt = Long.toString(System.currentTimeMillis());
            String md5 = MD5.create().digestHex(APP_ID + query + salt + APP_KEY);
            Request request = new Request.Builder()
                    .url("http://api.fanyi.baidu.com/api/trans/vip/translate?q="
                            + URLDecoder.decode(query, StandardCharsets.UTF_8) +
                            "&from=zh&to=en&appid=" + APP_ID + "&salt=" + salt + "&sign=" + md5
                    )
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            BaiduVo baiduVo = JSON.parseObject(Objects.requireNonNull(response.body()).string(), BaiduVo.class);
            return Optional.ofNullable(baiduVo)
                    .map(BaiduVo::getTransResult)
                    .flatMap(l -> l.stream().findFirst())
                    .map(TransResult::getDst)
                    .orElse(BConstant.LOREM_IPSUM_EN);
        } catch (Throwable t) {
            log.error("[翻译出错] ", t);
            return BConstant.LOREM_IPSUM_EN;
        }
    }

}
