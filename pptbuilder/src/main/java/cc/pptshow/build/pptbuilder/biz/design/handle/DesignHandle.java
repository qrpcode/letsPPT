package cc.pptshow.build.pptbuilder.biz.design.handle;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.biz.design.DesignBiz;
import cc.pptshow.build.pptbuilder.domain.*;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.exception.PPTBuildException;
import cc.pptshow.build.pptbuilder.service.PPTPageTypeService;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.*;
import cc.pptshow.ppt.show.PPTShowSide;
import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;

@Slf4j
@Service
public class DesignHandle {

    @Autowired
    private List<DesignBiz> designBizList;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    /**
     * 装饰处理
     */
    public List<PPTElement> design(List<PPTElement> pptElements,
                                   GlobalStyle globalStyle,
                                   Integer pageTypeId,
                                   PPTShowSide pptShowSide,
                                   PPTRegionPut pptRegionPut,
                                   int bigTitlePageCount) {
        checkAnnoAble();
        sortDesignBizList();
        PPTPage page = pptPageTypeService.findPPTPageById(pageTypeId);
        for (DesignBiz designBiz : designBizList) {
            Design design = getDesign(designBiz);
            List<PPTPage> excludePages = Arrays.asList(design.excludePage());
            List<PPTPage> onlyInPages = Arrays.asList(design.onlyInPage());

            //如果当前页面需要排除，就直接排除掉
            if (CollectionUtils.isNotEmpty(excludePages) && excludePages.contains(page)) {
                continue;
            }

            //如果仅处理某些页面，当前不是就排除掉
            if (CollectionUtils.isNotEmpty(onlyInPages) && !onlyInPages.contains(page)) {
                continue;
            }
            pptElements = designPptElements(pptElements, globalStyle, pageTypeId,
                    designBiz, design, pptShowSide, pptRegionPut, bigTitlePageCount);
        }
        return pptElements;
    }

    private List<PPTElement> designPptElements(List<PPTElement> pptElements,
                                               GlobalStyle globalStyle,
                                               Integer pageTypeId,
                                               DesignBiz designBiz,
                                               Design design,
                                               PPTShowSide pptShowSide,
                                               PPTRegionPut pptRegionPut,
                                               int bigTitlePageCount) {
        List<PPTBlockType> types = Arrays.asList(design.type());
        if (design.needIteration()) {
            List<Object> contexts = null;
            List<PPTElement> newElements = Lists.newArrayList(pptElements);
            for (PPTElement pptElement : pptElements) {
                Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT, pptElements);
                int index = newElements.indexOf(pptElement);
                if (types.contains(findType(pptElement))) {
                    DesignRequest designRequest = buildDesignRequest(pptElements, globalStyle, pageTypeId,
                            canvas, pptShowSide, pptRegionPut, bigTitlePageCount);
                    designRequest.setPptElement(pptElement);
                    designRequest.setContexts(contexts);
                    designRequest.setPptShowSide(pptShowSide);
                    DesignResponse response = designBiz.design(designRequest);
                    if (Objects.nonNull(response.getContexts())) {
                        contexts = response.getContexts();
                    }
                    if (Objects.nonNull(response.getPptElement())) {
                        newElements.set(index, response.getPptElement());
                    }
                }
            }
            log.info(designBiz.getClass().getName());
            pptElements = newElements;
        } else {
            Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT, pptElements);
            DesignRequest designRequest = buildDesignRequest(pptElements, globalStyle, pageTypeId,
                    canvas, pptShowSide, pptRegionPut, bigTitlePageCount);
            DesignResponse response = designBiz.design(designRequest);
            if (CollectionUtils.isNotEmpty(response.getPptElements())) {
                pptElements = response.getPptElements().stream().distinct().collect(Collectors.toList());
            }
        }
        return pptElements;
    }

    @NotNull
    private DesignRequest buildDesignRequest(List<PPTElement> pptElements,
                                             GlobalStyle globalStyle,
                                             Integer pageTypeId,
                                             Canvas canvas,
                                             PPTShowSide pptShowSide,
                                             PPTRegionPut pptRegionPut,
                                             int bigTitlePageCount) {
        DesignRequest designRequest = new DesignRequest();
        designRequest.setCanvas(canvas);
        designRequest.setPptElements(pptElements);
        designRequest.setGlobalStyle(globalStyle);
        designRequest.setPageId(pageTypeId);
        designRequest.setPptShowSide(pptShowSide);
        designRequest.setPptRegionPut(pptRegionPut);
        designRequest.setBigTitleNumber(bigTitlePageCount);
        return designRequest;
    }

    private PPTBlockType findType(PPTElement pptElement) {
        if (pptElement instanceof PPTText) {
            return PPTBlockType.TEXT;
        } else if (pptElement instanceof PPTImg) {
            return PPTBlockType.IMG;
        } else if (pptElement instanceof PPTShape) {
            return PPTBlockType.SHAPE;
        } else if (pptElement instanceof PPTLine) {
            return PPTBlockType.LINE;
        }
        throw new PPTBuildException("找到了一个PPTElement对象，但是没有对应PPTBlockType类型");
    }

    private void sortDesignBizList() {
        designBizList.sort((designBiz1, designBiz2) -> {
            Design anno1 = getDesign(designBiz1);
            Design anno2 = getDesign(designBiz2);
            return Integer.compare(anno1.order(), anno2.order());
        });
    }

    private Design getDesign(DesignBiz designBiz1) {
        return designBiz1.getClass().getAnnotation(Design.class);
    }

    private void checkAnnoAble() {
        for (DesignBiz designBiz : designBizList) {
            Assert.isTrue(designBiz.getClass().isAnnotationPresent(Design.class),
                    "不能在" + designBiz.getClass().getSimpleName() + "中找到@Design注解！");
        }
    }

}

