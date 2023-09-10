package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.show.PPTShowSide;
import cc.pptshow.ppt.show.PageSwitchingType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 给PPT页面统一设置2s自动播放
 * 自动添加动画
 */
@Slf4j
@Service
@Design(order = 17, needIteration = false)
public class SideSwitchDesignBizImpl implements DesignBiz {

    private static final long time = 2000;

    /**
     * 排除了【出现】
     */
    private static final List<PageSwitchingType> TYPES = Lists.newArrayList(
            PageSwitchingType.SMOOTH,
            PageSwitchingType.FADE_OUT,
            PageSwitchingType.ERASE,
            PageSwitchingType.SHAPE,
            PageSwitchingType.DISSOLVE,
            PageSwitchingType.NEWS_FLASH,
            PageSwitchingType.SPOKES,
            PageSwitchingType.BLIND,
            PageSwitchingType.COMB,
            PageSwitchingType.TAKING,
            PageSwitchingType.SEGMENTATION,
            PageSwitchingType.LINE,
            PageSwitchingType.BOARD,
            PageSwitchingType.LAUNCH,
            PageSwitchingType.INSERT,
            PageSwitchingType.PAGE_CURLING,
            PageSwitchingType.CUBE,
            PageSwitchingType.BOX,
            PageSwitchingType.PLAIN,
            PageSwitchingType.OPEN_DOOR,
            PageSwitchingType.STRIPPING
    );

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTShowSide pptShowSide = request.getPptShowSide();
        pptShowSide.setAutoPagerTime(time);
        pptShowSide.setPageSwitchingType(RandUtil.randElement(TYPES));
        return DesignResponse.buildByRequest(request);
    }

}
