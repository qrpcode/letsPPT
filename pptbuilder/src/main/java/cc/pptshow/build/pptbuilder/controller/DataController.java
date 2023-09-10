package cc.pptshow.build.pptbuilder.controller;


import cc.pptshow.build.pptbuilder.bean.DataTag;
import cc.pptshow.build.pptbuilder.domain.vo.Result;
import cc.pptshow.build.pptbuilder.service.DataTagService;
import cc.pptshow.build.pptbuilder.util.EsUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/data")
public class DataController {

    @Resource
    private DataTagService dataTagService;

    @GetMapping("/addTag")
    public Result savePathPPTFile(@RequestParam("tag") String tag) {
        DataTag dataTag = dataTagService.insert(tag);
        EsUtil.insertTag(dataTag);
        return Result.buildSuccessResult();
    }

    @GetMapping("/flush")
    public Result flushPinyin() throws InterruptedException {
        dataTagService.flushNullPinyin();
        return Result.buildSuccessResult();
    }

    @GetMapping("/delete")
    public Result delete() {
        dataTagService.deleteSameWord();
        return Result.buildSuccessResult();
    }

}
