package cc.pptshow.build.pptbuilder.biz.builder.element;

import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.BuilderQo;
import cc.pptshow.ppt.element.PPTElement;

import java.util.List;

public interface ElementBuilder {

    /**
     * 当前的filter可以处理哪些内容
     */
    List<PPTBlockType> canBuildTypes();

    /**
     * 对内容进行构建
     */
    List<PPTElement> buildElement(BuilderQo builderQo);

}
