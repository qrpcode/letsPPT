package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.ChannelConfig;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.exception.PPTBuildException;
import cc.pptshow.ppt.constant.Constant;
import cc.pptshow.ppt.util.FileUtil;
import cc.pptshow.ppt.util.ZipUtil;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

@Slf4j
public class PathUtil {

    private static final String PPT_BUILD = "pptBuild";

    public static String buildTodayPath() {
        ChannelConfig channelConfig = BConstant.config.get(ChannelType.GLOBAL.getCode());
        String date = DateUtil.format(new Date(), "yyyyMMdd");
        String pathName = channelConfig.getFilesPath() + date;
        new File(pathName).mkdirs();
        return pathName;
    }

    public static String buildPathToUnZip(String zipFilePath) {
        String path = FileUtil.tmpdir() + PPT_BUILD
                + Constant.SEPARATOR + UUID.randomUUID() + Constant.SEPARATOR;
        new File(path).mkdirs();
        log.info("path: {}", path);
        FileUtil.traditionalCopy(zipFilePath, path + "file.zip");
        String zipPath = path + "files" + Constant.SEPARATOR;
        ZipUtil.zipUncompress(path + "file.zip", zipPath);
        return zipPath;
    }

    public static String randPath(String suffix) {
        return FileUtil.tmpdir() + "randFile" + Constant.SEPARATOR + UUID.randomUUID() + "." + suffix;
    }

    /**
     * 检查一个目录是否是一个PPT的解压目录
     * @param unzipPath 目录地址
     */
    public static void checkIsPPTPath(String unzipPath) {
        File pptFiles = new File(unzipPath + Constant.SEPARATOR + "ppt" + Constant.SEPARATOR + "slides");
        if (!pptFiles.exists() || !pptFiles.isDirectory()) {
            throw new PPTBuildException(unzipPath + "不是一个合法的PPT文稿释放路径");
        }
    }

    /**
     * 获取路径下的文件列表，不会深入多层获取
     * @param path 文件路径
     */
    public static List<String> findPathFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            ArrayList<String> XMLs = Lists.newArrayList(Arrays.asList(Optional.ofNullable(file.list()).orElse(new String[0])));
            XMLs.remove("_rels");
            return XMLs;
        }
        return Lists.newArrayList();
    }
}
