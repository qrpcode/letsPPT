package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.ImgHelper;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.domain.PPTShapeCss;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.background.NoBackground;
import cc.pptshow.ppt.domain.border.ColorBorder;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTLine;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cc.pptshow.build.pptbuilder.constant.BConstant.PAGE_HEIGHT;
import static cc.pptshow.build.pptbuilder.constant.BConstant.PAGE_WIDTH;

/**
 * 看一下首页是不是太空旷了
 */
@Slf4j
@Service
@Design(order = 12, needIteration = false, onlyInPage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class MainPageBackgroundImgDesignBizImpl implements DesignBiz {

    /**
     * 最少也要单个元素跨度超过页面的横向或者纵向 75% 才认为页面不是空旷的
     */
    private static final double MIN_SIZE = 0.75;

    private static final int MARK_ALPHA = 9;
    private static final int BORDER_SIZE = 10;

    @Resource
    private ImgInfoService imgInfoService;

    @Resource
    private ImgHelper imgHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        if (isOpenPage(request.getPptElements())) {
            if (CollectionUtils.isEmpty(request.getGlobalStyle().getOpenHomePageUnderElements())) {
                List<PPTElement> underElements = buildHomePageBackgroundUnderElements(request.getGlobalStyle());
                List<PPTElement> upElements = buildHomePageBackgroundUpElements(request.getGlobalStyle());
                request.getGlobalStyle().setOpenHomePageUnderElements(underElements);
                request.getGlobalStyle().setOpenHomePageUpElements(upElements);
            }
            return addNotOpenElements(request);
        }
        return DesignResponse.buildByRequest(request);
    }

    @NotNull
    private DesignResponse addNotOpenElements(DesignRequest request) {
        List<PPTElement> pptElements = request.getPptElements();
        pptElements.addAll(0, request.getGlobalStyle().getOpenHomePageUnderElements());
        pptElements.addAll(request.getGlobalStyle().getOpenHomePageUpElements());
        return new DesignResponse(pptElements);
    }

    private List<PPTElement> buildHomePageBackgroundUpElements(GlobalStyle globalStyle) {
        List<PPTElement> elements = Lists.newArrayList();
        elements.add(buildOutLine(globalStyle.getColorInfo().getFromColor()));
        return elements;
    }

    private List<PPTElement> buildHomePageBackgroundUnderElements(GlobalStyle globalStyle) {
        List<PPTElement> elements = Lists.newArrayList();
        elements.add(buildAllSizeImg(globalStyle));
        elements.add(buildMark(globalStyle.getColorInfo().getBackgroundColor()));
        return elements;
    }

    private PPTElement buildOutLine(String fromColor) {
        PPTShape shape = new PPTShape();
        ColorBorder colorBorder = new ColorBorder();
        colorBorder.setColor(fromColor);
        colorBorder.setWidth(BORDER_SIZE);
        shape.setCss(PPTShapeCss.build()
                .setTop(0)
                .setLeft(0)
                .setWidth(PAGE_WIDTH)
                .setHeight(PAGE_HEIGHT)
                .setBackground(NoBackground.builder().build())
                .setBorder(colorBorder)
        );
        return shape;
    }

    /**
     * 创建一个遮罩层
     */
    private PPTElement buildMark(String color) {
        PPTShape shape = new PPTShape();
        shape.setCss(PPTShapeCss.build()
                .setTop(0)
                .setLeft(0)
                .setWidth(PAGE_WIDTH)
                .setHeight(PAGE_HEIGHT)
                .setBackground(ColorBackGround.buildByColor(color, MARK_ALPHA))
        );
        return shape;
    }

    private PPTElement buildAllSizeImg(GlobalStyle globalStyle) {
        PPTImg pptImg = new PPTImg();
        pptImg.setCss(PPTImgCss.build().setTop(0).setLeft(0).setWidth(PAGE_WIDTH).setHeight(PAGE_HEIGHT));
        String randInnerImg = imgHelper.findRandImg(globalStyle);
        String cut = imgHelper.cutImgByWidthAndHeight(randInnerImg, PAGE_WIDTH, PAGE_HEIGHT);
        pptImg.setFile(cut);
        return pptImg;
    }

    private boolean isOpenPage(List<PPTElement> elements) {
        return elements.stream()
                .map(e -> {
                    if (e instanceof PPTText) {
                        return Canvas.buildCanvasByPPTTextOnlyTextPlace((PPTText) e);
                    } else if (e instanceof PPTLine) {
                        return null;
                    } else {
                        return Canvas.buildCanvasByElement(e);
                    }
                })
                .filter(Objects::nonNull)
                .noneMatch(shape -> shape.getRight() - shape.getLeft() > MIN_SIZE * PAGE_WIDTH
                        || shape.getBottom() - shape.getTop() > MIN_SIZE * PAGE_HEIGHT);
    }

}
