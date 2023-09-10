package cc.pptshow.build.pptbuilder.biz.builder;

import cc.pptshow.build.pptbuilder.domain.vo.TemplateBuildQo;
import cc.pptshow.build.pptbuilder.domain.vo.TemplateNearlyVo;

import java.util.List;

public interface TemplateBuildHelper {

    /**
     * 推断模板设定内容
     * @param templateBuildQo 传入用户当前设定模板
     */
    List<TemplateNearlyVo> inferTemplate(TemplateBuildQo templateBuildQo);

}
