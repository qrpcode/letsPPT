package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.biz.builder.TemplateBuildHelper;
import cc.pptshow.build.pptbuilder.domain.qo.PageTemplateQo;
import cc.pptshow.build.pptbuilder.biz.builder.TemplateBuildBiz;
import cc.pptshow.build.pptbuilder.domain.vo.Result;
import cc.pptshow.build.pptbuilder.domain.vo.TemplateBuildQo;
import cc.pptshow.build.pptbuilder.domain.vo.TemplateNearlyVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/template")
public class TemplateController {

    @Resource
    private TemplateBuildBiz templateBuildBiz;

    @Resource
    private TemplateBuildHelper templateBuildHelper;

    @PostMapping("/build/page")
    public Result savePageTemplate(@RequestBody PageTemplateQo pageTemplateQo) {
        templateBuildBiz.buildPPTPage(pageTemplateQo);
        return Result.buildSuccessResult();
    }

    @PostMapping("/build/helper")
    public List<TemplateNearlyVo> templateBuildHelper(@RequestBody TemplateBuildQo templateBuildQo) {
        return templateBuildHelper.inferTemplate(templateBuildQo);
    }

}
