package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.biz.helper.LayerHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.domain.PPTTextCss;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 针对可能文字在图形纵向中心的判定
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT,
        order = 6,
        excludePage = {PPTPage.HOME, PPTPage.INNER_TITLE})
public class TextHeightCenterDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Resource
    private LayerHelper layerHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        List<PPTText> texts = findAllText(request);
        List<PPTElement> backgroundElements =
                layerHelper.findHalfIntersectBackgroundElements(request.getCanvas(), pptText);
        adjustmentTextLineHeightCenter(pptText, texts, backgroundElements);
        return new DesignResponse(pptText);
    }

    @NotNull
    private List<PPTText> findAllText(DesignRequest request) {
        return request.getPptElements().stream()
                .filter(e -> e instanceof PPTText)
                .map(e -> (PPTText) e)
                .collect(Collectors.toList());
    }

    /**
     * 文字纵向居中调整
     * 如果文字高度和图形差不多，那么它应该处于这个图形的中心
     */
    private void adjustmentTextLineHeightCenter(PPTText pptText,
                                                List<PPTText> texts,
                                                List<PPTElement> backgroundElements) {
        PPTShape pptShape = findMaybeBackgroundShape(pptText, backgroundElements);
        texts.remove(pptText);
        if (Objects.nonNull(pptShape) && isNotOtherBackground(texts, pptShape)) {
            double middleHeight = pptShape.getCss().getTop() + (pptShape.getCss().getHeight() / 2);
            pptText.getCss().setTop(middleHeight - (pptText.getCss().getHeight() / 2));
        }
    }

    private boolean isNotOtherBackground(List<PPTText> texts, PPTShape pptShape) {
        for (PPTText text : texts) {
            if (Objects.nonNull(findMaybeBackgroundShape(text, Lists.newArrayList(pptShape)))) {
                return false;
            }
        }
        return true;
    }

    private PPTShape findMaybeBackgroundShape(PPTText pptText, List<PPTElement> backgroundElements) {
        return backgroundElements.stream()
                .filter(e -> e instanceof PPTShape)
                .map(e -> (PPTShape) e)
                .filter(e -> {
                    PPTText cloneText = pptText.clone();
                    PPTTextCss css = textDesignHelper.chooseTextCssOnlyTextNeed(cloneText).getCss();
                    double shapeHeight = e.getCss().getHeight();
                    double textHeight = css.getHeight();
                    double shapeWidth = e.getCss().getWidth();
                    double textWidth = css.getHeight();
                    double textProportionY = textHeight / shapeHeight;
                    double shapeProportionY = shapeHeight / textHeight;
                    double textProportionX = textWidth / shapeWidth;
                    double shapeProportionX = shapeWidth / textWidth;
                    return ((textProportionY >= 0.8 && textProportionY <= 1.5)
                            || (shapeProportionY >= 0.8 && shapeProportionY <= 2))
                            && ((textProportionX >= 0.3 && textProportionX <= 1.2)
                            || (shapeProportionX >= 0.8 && shapeProportionX <= 1.2));
                }).findFirst().orElse(null);
    }

}
