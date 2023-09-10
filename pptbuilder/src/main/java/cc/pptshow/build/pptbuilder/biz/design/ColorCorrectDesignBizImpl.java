package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockImg;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTBlockShape;
import cc.pptshow.build.pptbuilder.context.PPTBlockContext;
import cc.pptshow.build.pptbuilder.dao.PPTBlockShapeMapper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.RGB;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.exception.PPTBuildException;
import cc.pptshow.build.pptbuilder.service.PPTBlockImgService;
import cc.pptshow.build.pptbuilder.service.PPTBlockService;
import cc.pptshow.build.pptbuilder.service.PPTBlockShapeService;
import cc.pptshow.build.pptbuilder.util.ColorUtil;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.background.SerializableBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTShape;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.util.ColorUtil.*;

/**
 * 颜色纠正，如果当前作色和原始的不一样，就将他们变成ppt初始状态
 */
@Slf4j
@Service
@Design(order = 11, type = {PPTBlockType.SHAPE})
public class ColorCorrectDesignBizImpl implements DesignBiz {

    @Resource
    private PPTBlockShapeService pptBlockShapeService;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTShape pptShape = (PPTShape) request.getPptElement();
        PPTBlockPut pptBlockPut = PPTBlockContext.findUsePPTBlockPut(pptShape);
        if (Objects.isNull(pptBlockPut)) {
            return DesignResponse.buildByRequest(request);
        }
        Map<String, String> colorMap = PPTBlockContext.findColorMap(pptBlockPut);
        PPTBlock pptBlock = findElementBlock(pptShape, pptBlockPut);
        PPTBlockShape pptBlockShape = pptBlockShapeService.selectByBlockId(pptBlock.getId());
        if (Objects.isNull(pptBlockShape)) {
            log.error("【颜色追溯发现空对象】 pptShape：{}", JSON.toJSONString(pptShape));
            return DesignResponse.buildByRequest(request);
        }
        SerializableBackground background = JSON.parseObject(pptBlockShape.getBackgroundColor(), SerializableBackground.class);
        Background oldBackground = background.buildBackground();

        if (!(oldBackground instanceof ColorBackGround)) {
            return DesignResponse.buildByRequest(request);
        }

        String shouldUseColor = colorMap.get(((ColorBackGround) oldBackground).getColor());
        Background nowBackground = pptShape.getCss().getBackground();

        if (!(nowBackground instanceof ColorBackGround)) {
            return DesignResponse.buildByRequest(request);
        }
        ColorBackGround colorBackGround = (ColorBackGround) nowBackground;

        if (Strings.isBlank(shouldUseColor)) {
            //之前没有过，需要新储存一个
            Set<String> nowColors = Sets.newHashSet(colorMap.values());
            String nowRealColor = colorBackGround.getColor();
            if (nowColors.contains(nowRealColor)) {
                //换个颜色
                String color = findNewColor(nowColors, request.getGlobalStyle());
                colorBackGround.setColor(color);
            } else {
                //不需要换颜色
                PPTBlockContext.saveColorLog(pptBlockPut, ((ColorBackGround) oldBackground).getColor(), nowRealColor);
            }
        } else {
            colorBackGround.setColor(shouldUseColor);
        }

        return new DesignResponse(pptShape);
    }

    private String findNewColor(Set<String> nowColors, GlobalStyle globalStyle) {
        String fromColor = globalStyle.getColorInfo().getFromColor();
        String toColor = globalStyle.getColorInfo().getToColor();
        if (!nowColors.contains(fromColor)) {
            return fromColor;
        } else if (Strings.isNotEmpty(toColor) && !nowColors.contains(toColor)) {
            return toColor;
        } else {
            RGB rgb = ColorUtil.convertHexToRGB(fromColor);
            if (Objects.isNull(rgb)) {
                log.error("[rgb失败] {}", fromColor);
            }
            RGB light1 = getLightColor(rgb, 0.25);
            RGB deep1 = getDeepColor(rgb, 0.75);
            RGB deep2 = getDeepColor(rgb, 0.5);
            RGB light2 = getLightColor(rgb, 0.5);
            return Lists.newArrayList(toHex(light1), toHex(deep1), toHex(deep2), toHex(light2)).stream()
                    .filter(c -> !nowColors.contains(c)).findFirst().orElseThrow(PPTBuildException::new);
        }
    }

    private PPTBlock findElementBlock(PPTShape pptShape, PPTBlockPut pptBlockPut) {
        Map<PPTBlock, PPTElement> pptBlockPPTElementMap =
                PPTBlockContext.filterHaveElementMap(pptShape).get(pptBlockPut);
        return pptBlockPPTElementMap.keySet().stream()
                .filter(block -> pptBlockPPTElementMap.get(block).equals(pptShape))
                .collect(Collectors.toList())
                .stream()
                .findFirst()
                .orElseThrow(PPTBuildException::new);
    }


}
