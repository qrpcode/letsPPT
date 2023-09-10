package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.EnumPPTStyle;
import cc.pptshow.build.pptbuilder.bean.MusicInfo;
import cc.pptshow.build.pptbuilder.biz.design.help.FromHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.MusicInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 给页面加上背景音乐
 * 因为是背景音，循环播放，所以只需要处理首页就可以
 */
@Slf4j
@Service
@Design(order = 19, needIteration = false, onlyInPage = PPTPage.HOME)
public class BgmDesignBizImpl implements DesignBiz {

    @Resource
    private MusicInfoService musicInfoService;

    @Resource
    private FromHelper fromHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        EnumPPTStyle pptStyle = request.getGlobalStyle().getPptStyle();
        MusicInfo musicInfo = musicInfoService.findRandOneByStyle(pptStyle.getId());
        if (Objects.isNull(musicInfo)) {
            log.error("[没有找到合适的背景音乐] style:{}", pptStyle);
        }
        fromHelper.addFrom(request.getGlobalStyle(), musicInfo);
        request.getPptShowSide().setBackgroundMusic(BConstant.MUSIC_SYS_PATH + musicInfo.getMusicPath());
        return DesignResponse.buildByRequest(request);
    }

}
