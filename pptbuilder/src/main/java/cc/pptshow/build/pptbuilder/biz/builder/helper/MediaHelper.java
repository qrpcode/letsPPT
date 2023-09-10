package cc.pptshow.build.pptbuilder.biz.builder.helper;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.library.OfficeLibrary;
import cc.pptshow.ppt.util.PPTUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.UUID;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;
import static org.apache.xerces.util.XMLSymbols.EMPTY_STRING;

@Slf4j
@Service
public class MediaHelper {

    @Resource
    private OfficeLibrary officeLibrary;

    private static final String IMG_HOME = "https://img.mubangou.com/";

    private static final String IMG_BUCKET = "mbg-img";
    private static final String FILE_BUCKET = "mbg-down";

    public synchronized String ppt2Png(String pptPath, ChannelType channelType) {
        String pptOnlyPath = getOnlyFileNamePath(pptPath);
        log.info("pptOnlyPath: {}", pptOnlyPath);
        FileUtil.file(pptOnlyPath).mkdirs();
        officeLibrary.syncPPT2Jpg(pptPath, pptOnlyPath, null);
        String pngPath = pptOnlyPath + ".png";
        String jpgPath = pptOnlyPath + ".jpg";
        PPTUtil.png2LongImg(pptOnlyPath, pngPath, WATER_MARK.get(channelType.getCode()));
        ImgUtil.convert(FileUtil.file(pngPath), FileUtil.file(jpgPath));
        return jpgPath;
    }

    private String getOnlyFileNamePath(String pptPath) {
        return pptPath.substring(0, pptPath.length() - 5);
    }

    @SneakyThrows
    public synchronized String ppt2MP4(String pptPath) {
        String path = replaceToBackSlashLinePath(pptPath);
        String pptOnlyPath = getOnlyFileNamePath(path);
        String mp4Path = pptOnlyPath + ".mp4";
        PPTUtil.PPT2MP4(path, mp4Path);
        Thread.sleep(20000);
        return mp4Path;
    }

    @SneakyThrows
    public String mp4Compression(String mp4Path) {
        mp4Path = replaceNoBackSlashLinePath(mp4Path);
        String sMp4 = mp4Path.replace(".mp4", "s.mp4");
        log.info("[MP4生成压缩版本] mp4Path:{}, sMp4: {}", mp4Path, sMp4);
        PPTUtil.mp4Compression(mp4Path, sMp4);
        log.info("[MP4生成压缩版本] 生成调用结束，等待生成");
        Thread.sleep(10000);
        log.info("[MP4生成压缩版本] 生成结束，等待上传");
        return sMp4;
    }

    private String replaceToBackSlashLinePath(String path) {
        return path.replace("/", "\\");
    }

    private String replaceNoBackSlashLinePath(String path) {
        return path.replace("\\", "/");
    }

    public String uploadImg(String file) {
        return upload(IMG_BUCKET, IMG_HOME, file);
    }

    public String uploadFile(String file) {
        return upload(FILE_BUCKET, "", file);
    }

    public String upload(String bucket, String domain, String file) {
        //七牛云代码
        Configuration cfg = new Configuration();
        UploadManager uploadManager = new UploadManager(cfg);
        String fileName = UUID.randomUUID() + "." + FileNameUtil.extName(file);
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(file, fileName, upToken);
            // 解析上传成功的结果
            JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return domain + fileName;
        } catch (QiniuException ex) {
            log.error("七牛云上传出错", ex);
        }
        throw new RuntimeException("上传七牛云出错");
    }

    public String updatePPT(String pptPath) {
        return uploadFile(pptPath);
    }

    public void delete(String bucket, String file) {
        Configuration cfg = new Configuration();
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, file);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            log.error("七牛云删除文件出错", ex);
        }
    }

    public void deleteFile(String downUrl) {
        delete(FILE_BUCKET, downUrl);
    }

    public void deleteImg(String imgUrl) {
        delete(IMG_BUCKET, imgUrl.replace(IMG_HOME, EMPTY_STRING));
    }
}
