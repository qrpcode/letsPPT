package cc.pptshow.build.pptbuilder.biz.img;

import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import cc.pptshow.build.pptbuilder.domain.qo.ImgInfoQo;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.build.pptbuilder.util.WebImgUtil;
import cn.hutool.http.HttpUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static org.apache.xpath.compiler.PsuedoNames.PSEUDONAME_ROOT;

/**
 * Pexels图片下载
 */
@Slf4j
@Service
public class PexelsImgBiz {

    @Resource
    private ImgInfoService imgInfoService;

    public Long saveImg(ImgInfoQo imgInfoQo) {
        checkImgInfo(imgInfoQo);
        if (Strings.isNotBlank(imgInfoQo.getDownUrl())) {
            String path = downImg(imgInfoQo.getFromUrl(), imgInfoQo.getDownUrl());
            imgInfoQo.setPicUri(path);
        }
        imgInfoQo.setLicense("pexels license");
        imgInfoQo.setPicFrom("pexels");
        ImgInfo insert = imgInfoService.insert(imgInfoQo);
        return insert.getId();
    }

    private String downImg(String fromUrl, String downUrl) {
        String imgId = findImgId(fromUrl);
        String imgPath = imgId + ".jpg";
        log.info("[imgDown] {}", downUrl);
        WebImgUtil.downImg(downUrl, imgPath);
        return imgPath;
    }

    private void checkImgInfo(ImgInfo imgInfo) {
        assert Objects.nonNull(imgInfo)
                && Strings.isNotBlank(imgInfo.getFromUrl())
                && Objects.nonNull(imgInfo.getUseBackground())
                && Objects.nonNull(imgInfo.getColorType())
                && Strings.isNotBlank(imgInfo.getTitle());
    }

    private String findImgId(String imgUrl) {
        List<String> strings = Lists.newArrayList(Splitter.on(PSEUDONAME_ROOT).split(imgUrl));
        return strings.get(strings.size() - 2);
    }

}
