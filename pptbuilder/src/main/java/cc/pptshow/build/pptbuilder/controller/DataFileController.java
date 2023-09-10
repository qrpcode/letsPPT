package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.domain.vo.Result;
import cc.pptshow.build.pptbuilder.service.DataFilePathService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cc.pptshow.build.pptbuilder.constant.BConstant.SYS_PATH;

@RestController
@CrossOrigin
@RequestMapping("/dataFile")
public class DataFileController {

    @Resource
    private DataFilePathService dataFilePathService;

    @GetMapping("/pathFileAdd")
    public Result savePathPPTFile(@RequestParam("path") String path) {
        dataFilePathService.findAllFileByPath(path);
        return Result.buildSuccessResult();
    }

    @GetMapping("/abandonPPT")
    public Result abandonPPTFile(@RequestParam("path") String path) {
        dataFilePathService.abandonPPTFile(path);
        return Result.buildSuccessResult();
    }

    @GetMapping("/getLast")
    public Result getLastPPTFile() {
/*
        String lastUnmark = dataFilePathService.getLastUnmark();
        dataFilePathService.markPPTFile(lastUnmark);
        return Result.buildSuccessData(dataFilePathService.getLastUnmark());
*/
        return Result.buildSuccessData(SYS_PATH + "bangongziyuan\\新首页模板.pptx");
    }

}
