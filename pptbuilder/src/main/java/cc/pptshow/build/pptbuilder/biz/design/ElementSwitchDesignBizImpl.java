package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.ElementGroupHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.domain.animation.AnimationAttribute;
import cc.pptshow.ppt.domain.animation.InAnimation;
import cc.pptshow.ppt.domain.animation.InAnimationType;
import cc.pptshow.ppt.domain.animation.ShowAnimationType;
import cc.pptshow.ppt.element.PPTElement;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * PPT增加动画效果
 */
@Slf4j
@Service
@Design(order = 18, needIteration = false)
public class ElementSwitchDesignBizImpl implements DesignBiz {

    /**
     * 最大组数
     */
    private static final int MAX_GROUP = 5;

    @Resource
    private ElementGroupHelper elementGroupHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<List<PPTElement>> elementGroups = elementGroupHelper.buildElementGroup(request.getPptElements());
        sortElements(elementGroups, request.getPageId());
        int index = 1;
        InAnimationType inAnimationType = RandUtil.round(BConstant.ElementInAnimationTypes);
        if (elementGroups.size() > MAX_GROUP) {
            elementGroups = elementGroupHelper.merge(elementGroups, MAX_GROUP);
        }
        for (List<PPTElement> elementGroup : elementGroups) {
            List<PPTElement> notTextElements = elementGroupHelper.findElementsNotText(elementGroup);
            //List<List<PPTElement>> texts = elementGroupHelper.buildElementGroupOnlyText(elementGroup);
            //sortElements(texts, request.getPageId());
            addAnimation(notTextElements, inAnimationType, ++index);
            //for (List<PPTElement> innerTexts : texts) {
            //    addAnimation(innerTexts, inAnimationType, ++index);
            //}
            ArrayList<PPTElement> pptElements = Lists.newArrayList(elementGroup);
            pptElements.removeAll(notTextElements);
            addAnimation(pptElements, inAnimationType, ++index);
        }
        return DesignResponse.buildByRequest(request);
    }

    private void sortElements(List<List<PPTElement>> elementGroups, Integer pageId) {
        if (pageId == PPTPage.CONTENTS.getId() || pageId == PPTPage.HOME.getId()
                || pageId == PPTPage.BIG_TITLE.getId() || pageId == PPTPage.THANK.getId()) {
            elementGroupHelper.sortByTop(elementGroups);
        } else {
            elementGroupHelper.sortByLeft(elementGroups);
        }
    }

    private void addAnimation(List<PPTElement> notTextElements, InAnimationType inAnimationType, int index) {
        for (PPTElement element : notTextElements) {
            InAnimation inAnimation = new InAnimation();
            inAnimation.setTimeMs(500);
            inAnimation.setInAnimationType(inAnimationType);
            inAnimation.setAnimationAttribute(AnimationAttribute.LEFT);
            inAnimation.setIndex(index);
            inAnimation.setShowAnimationType(ShowAnimationType.AFTER);
            element.setInAnimation(inAnimation);
        }
    }

}
