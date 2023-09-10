package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.ChannelConfig;
import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class ConfigUtil {

    private static final String CONFIG_LINK = "http://localhost:998/api/config/";

    @SneakyThrows
    public static void flushConfig() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(CONFIG_LINK + getName() + ".json")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        String json = Objects.requireNonNull(response.body()).string();
        JSONObject jsonObject = JSON.parseObject(json);
        BConstant.config = jsonObject.toJavaObject(new TypeReference<Map<String, ChannelConfig>>() {
        });
        log.info("[拉取的配置信息] {}", BConstant.config);
    }

    /**
     * 获取当前机器名
     * windows系统取  C:/ppt_build.txt  信息
     * Linux系统取   /www/ppt_build.txt  信息
     *
     * @return 机器名信息
     */
    public static String getName() {
        FileReader fileReader;
        //if (FontUtilities.isWindows) {
            fileReader = new FileReader("C:\\ppt_build.txt");
        //} else {
        //     fileReader = new FileReader("/www/ppt_build.txt");
        //}
        return fileReader.readString();
    }

}
