package cc.pptshow.build.pptbuilder.util;

import cn.hutool.http.HttpUtil;

import static cc.pptshow.build.pptbuilder.constant.BConstant.IMG_SYS_PATH;

public class WebImgUtil {

    public static void downImg(String imgUrl, String imgPath) {
        HttpUtil.downloadFile(imgUrl, IMG_SYS_PATH + imgPath);
    }

}
