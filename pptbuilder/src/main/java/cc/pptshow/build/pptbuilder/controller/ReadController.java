package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.domain.PPTPageRead;
import cc.pptshow.build.pptbuilder.biz.read.ReadBiz;
import cc.pptshow.build.pptbuilder.domain.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("read/ppt")
public class ReadController {

    @Resource
    private ReadBiz readBiz;

    @GetMapping("/filePath")
    public List<PPTPageRead> buildPPTRegionBlock(@RequestParam("file") String file) {
        return readBiz.readPPTToReadElements(file);
    }

    @GetMapping("/cache")
    public Result cachePPTImg() {
        readBiz.cachePPTImg();
        return Result.buildSuccessResult();
    }

}
