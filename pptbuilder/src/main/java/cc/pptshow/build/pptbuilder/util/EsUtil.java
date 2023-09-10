package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.bean.DataTag;
import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Date;
import java.util.Objects;

/**
 * 这个项目引入Es包就爆炸，查不出原因，反正刷数据部分不多直接http请求了
 */
@Slf4j
public class EsUtil {

    private static final String PPT_UPDATE_API = "https://mubangou.com/api/es/addPPT.php?key=LwDYALNrGuXs8hkp6V2x&id=";
    private static final String TAG_UPDATE_API = "https://mubangou.com/api/es/addTag.php?key=LwDYALNrGuXs8hkp6V2x&id=";
    //private static final String TAG_UPDATE_API = "http://127.0.0.1:998/api/es/addTag.php?key=LwDYALNrGuXs8hkp6V2x&id=";
    private static final String TAG_DELETE_API = "https://mubangou.com/api/es/deleteTag.php?key=LwDYALNrGuXs8hkp6V2x&id=";

    @SneakyThrows
    public static void insertPPT(FilePPTInfo filePPTInfo) {
        if (Objects.isNull(filePPTInfo)) {
            return;
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String timeStr = DateUtil.formatDate(new Date());
        if (Objects.nonNull(filePPTInfo.getCreateTime())) {
            timeStr = DateUtil.formatDate(filePPTInfo.getCreateTime());
        }
        String bodyStr = "{\n" +
                "    \"id\": " + filePPTInfo.getId() + ",\n" +
                "    \"uid\": \"" + filePPTInfo.getUid() + "\",\n" +
                "    \"designer_id\": " + filePPTInfo.getDesignerId() + ",\n" +
                "    \"title\": \"" + filePPTInfo.getTitle() + "\",\n" +
                "    \"create_time\": \"" + timeStr + "\",\n" +
                "    \"channel\": \"" + filePPTInfo.getChannel() + "\",\n" +
                "    \"color_type\": " + filePPTInfo.getColorType() + ",\n" +
                "    \"ppt_style\":" + filePPTInfo.getPptStyle() + ",\n" +
                "    \"page_size\": " + filePPTInfo.getPageSize() + ",\n" +
                "    \"file_format\": \"" + filePPTInfo.getFileFormat() + "\",\n" +
                "    \"down_url\": \"" + filePPTInfo.getDownUrl() + "\",\n" +
                "    \"video_url\": \"" + filePPTInfo.getVideoUrl() + "\",\n" +
                "    \"pic_url\": \"" + filePPTInfo.getPicUrl() + "\",\n" +
                "    \"tags\": \"" + filePPTInfo.getTags() + "\",\n" +
                "    \"about_text\": \"" + filePPTInfo.getAboutText() + "\",\n" +
                "    \"source_json\":" + filePPTInfo.getSourceJson() +
                "}";
        log.info("filePPTInfo: {}", bodyStr);
        RequestBody body = RequestBody.create(mediaType, bodyStr);
        Request request = new Request.Builder()
                .url(PPT_UPDATE_API + filePPTInfo.getUid())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        response.close();
    }

    @SneakyThrows
    public static void insertTag(DataTag dataTag) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "{\"id\":" + dataTag.getId()
                + ",\"tag\": \"" + dataTag.getTag() + "\"," +
                "\"pinyin\": \"" + dataTag.getPinyin() + "\"}");
        Request request = new Request.Builder()
                .url(TAG_UPDATE_API + dataTag.getId())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        response.close();
    }

    @SneakyThrows
    public static void deleteTag(int id) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(TAG_DELETE_API + id)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        response.close();
    }
}
