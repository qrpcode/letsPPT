package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.dao.ImgInfoMapper;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ImgInfoServiceImpl implements ImgInfoService {

    private static final String DEFAULT = "pexels-jonathan-cooper-12568118.jpg";

    @Resource
    private ImgInfoMapper imgInfoMapper;

    @Override
    public String queryOnceBackgroundByRand(Integer colorType) {
        ImgInfo image = new ImgInfo();
        image.setColorType(colorType);
        image.setUseBackground(1);
        log.info("[queryOnceBackgroundByRand] colorType:{}", colorType);
        List<ImgInfo> infos = imgInfoMapper.select(image);
        if (CollectionUtils.isEmpty(infos)) {
            return BConstant.IMG_SYS_PATH + DEFAULT;
        }
        return BConstant.IMG_SYS_PATH + RandUtil.round(infos).getPicUri();
    }

    @Override
    public String randInnerImg() {
        return BConstant.IMG_SYS_PATH + imgInfoMapper.randWorkImg().getPicUri();
    }

    @Override
    public ImgInfo insert(ImgInfo imgInfo) {
        imgInfoMapper.insert(imgInfo);
        return imgInfo;
    }

    @Override
    public List<ImgInfo> findByKeyword(String keyword) {
        return imgInfoMapper.selectByKeyword(keyword);
    }

    @Override
    public List<ImgInfo> findByColorTypes(String colorType) {
        return imgInfoMapper.selectByTypes(colorType);
    }
}
