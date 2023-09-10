package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.bean.EnumPPTBackground;
import cc.pptshow.build.pptbuilder.bean.EnumPPTStyle;
import cc.pptshow.build.pptbuilder.bean.EnumSpecialType;
import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import cc.pptshow.build.pptbuilder.domain.PPTPageTypeVo;
import cc.pptshow.build.pptbuilder.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/enum")
public class EnumController {

    @Resource
    private EnumPPTBackgroundService enumPPTBackgroundService;

    @Resource
    private EnumPPTStyleService enumPPTStyleService;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private PPTPageModelService pptPageModelService;

    @Resource
    private EnumSpecialTypeService enumSpecialTypeService;

    @GetMapping("/style")
    public List<EnumPPTStyle> findAllStyle() {
        return enumPPTStyleService.findAll();
    }

    @GetMapping("/background")
    public List<EnumPPTBackground> findAllBackGround() {
        return enumPPTBackgroundService.findAll();
    }

    @GetMapping("/pages")
    public List<PPTPageTypeVo> findAllPage() {
        return pptPageTypeService.findAll().stream()
                .map(p -> {
                    PPTPageTypeVo pptPageTypeVo = new PPTPageTypeVo(p);
                    pptPageTypeVo.setModels(pptPageModelService.findByPageId(p.getId()));
                    return pptPageTypeVo;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/specialType")
    public List<EnumSpecialType> findAllSpecialType() {
        return enumSpecialTypeService.findAll();
    }

}
