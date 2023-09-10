package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.bean.DataTitle;
import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.bean.KeywordStyle;
import cc.pptshow.build.pptbuilder.biz.analysis.TitleAnalysis;
import cc.pptshow.build.pptbuilder.biz.builder.PPTBuildBiz;
import cc.pptshow.build.pptbuilder.biz.builder.TitleBuilder;
import cc.pptshow.build.pptbuilder.biz.helper.BaiduImgHelper;
import cc.pptshow.build.pptbuilder.domain.BuildRequire;
import cc.pptshow.build.pptbuilder.domain.StyleAnalysis;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.service.DataTitleService;
import cc.pptshow.build.pptbuilder.service.KeywordStyleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/title")
public class TitleController {

    @Resource
    private TitleAnalysis titleAnalysis;

    @Resource
    private KeywordStyleService keywordStyleService;

    @Resource
    private BaiduImgHelper baiduImgHelper;

    @Resource
    private TitleBuilder titleBuilder;

    @GetMapping("/analysis")
    public StyleAnalysis analysisTitle(@RequestParam("id") Long id) {
        return titleAnalysis.analysisTitle(id);
    }

    @PostMapping("/keyword")
    public String saveKeywordStyle(@RequestBody KeywordStyle keywordStyle) {
        keywordStyleService.save(keywordStyle);
        return "ok";
    }

    @GetMapping("/baiduImg")
    public void baiduImg(@RequestParam("title") String title) {
        baiduImgHelper.downImage(title);
    }

    @GetMapping("/build")
    public FilePPTInfo buildTitle(@RequestParam("id") Long id) {
        return titleBuilder.buildByTitleId(id, ChannelType.MBG);
    }

    @GetMapping("/buildAll")
    public String buildAll() {
        titleBuilder.buildAll();
        return "ok";
    }

    @GetMapping("/buildTodayGlobal")
    public String buildTodayGlobal() {
        titleBuilder.buildTodayGlobal();
        return "ok";
    }

    @GetMapping("/buildByRemark")
    public String buildByRemark() {
        titleBuilder.buildByRemark();
        return "ok";
    }

}
