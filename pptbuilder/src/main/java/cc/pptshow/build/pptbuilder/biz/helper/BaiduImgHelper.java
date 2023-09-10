package cc.pptshow.build.pptbuilder.biz.helper;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.util.ImageBase64Utils;
import cc.pptshow.ppt.constant.Constant;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BaiduImgHelper {

    @SneakyThrows
    public List<File> downImage(String keyword) {
        String result = findByWord(keyword);
        String tempPath = BConstant.TMPDIR;
        tempPath = tempPath + keyword + Constant.SEPARATOR;
        File f = new File(tempPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray array = jsonObject.getJSONArray("data");
        //只抓取第一张
        List<File> files = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            JSONObject o = array.getJSONObject(i);
            try {
                download(o.getString("hoverURL"), tempPath + i + ".jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
            files.add(FileUtil.file(tempPath + i + ".jpg"));
        }
        return files;
    }

    private String findByWord(String keyword) throws IOException {
        int page = 0;
        String url = "https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result&" +
                "queryWord=" + keyword + "&cl=2&lm=-1&ie=utf-8&oe=utf-8&adpicid=&st=&z=&ic=&hd=&latest=&copyright=&" +
                "word=" + keyword + "&s=&se=&tab=&width=&height=&face=&istype=&qc=&nc=&fr=&expermode=&force=&" +
                "pn=" + page + "&rn=30";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "text/plain, */*; q=0.01")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .addHeader("Connection", "keep-alive")
                .addHeader("Referer", "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=2013" +
                        "26592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1667614009368_R&pv=&ic=&nc=1&z=&hd=&latest=&cop" +
                        "yright=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&dyTabStr=MCwzLDEsNiw0LDcsOCwyLDUs" +
                        "OQ%3D%3D&ie=utf-8&sid=&word=%E7%AE%80%E7%BA%A6%E9%A3%8E%E4%BF%9D%E6%8A%A4%E7%8E%AF%E5%A2%83" +
                        "%E4%B8%96%E7%95%8C%E5%9C%B0%E7%90%83%E6%97%A5")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36" +
                        " (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.26")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("sec-ch-ua", "\"Microsoft Edge\";v=\"107\", \"Chromium\";v=\"107\", \"Not=" +
                        "A?Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .build();
        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).string();
    }

    public static void download(String url, String filePath){
        log.info("[download] url:{} path:{}", url, filePath);
        String fileParam = filePath + "_barcode";
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url+fileParam)
                    .method("GET", null)
                    .addHeader("Accept", "*/*")
                    .addHeader("Content-Type", "image/jpeg")
                    .build();
            ResponseBody body = client.newCall(request).execute().body();
            byte[] bytes = body.bytes();
            String result = ImageBase64Utils.bytesToBase64(bytes);
            //输出图片的路径
            String fileOutPath = filePath.replaceAll("sdpc","jpeg");
            ImageBase64Utils.base64ToImageFile(result, fileOutPath);
        } catch (Exception e) {
            log.error("[抓取图片过程失败]", e);
        }
    }

}
