package cc.pptshow.build.pptbuilder.biz.builder;

import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.qo.PPTPageBuildQo;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;

/**
 * 针对页面进行生成
 */
public interface PageBuildBiz {

    /**
     * 根据页面类型和关键词生成页面
     */
    PPTShowSide buildPageByStyle(Integer pageTypeId,
                                 GlobalStyle globalStyle,
                                 PPTShow ppt,
                                 int bigTitlePageCount,
                                 int pageNum);

    PPTShowSide pptPageBuild(PPTPageBuildQo pptPageBuildQo);

}
