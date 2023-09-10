package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.biz.img.PexelsImgBiz;
import cc.pptshow.build.pptbuilder.domain.qo.ImgInfoQo;
import cc.pptshow.build.pptbuilder.domain.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/img")
public class ImgController {

    @Resource
    private PexelsImgBiz pexelsImgBiz;

    @PostMapping("/pexels")
    public Result addImg(@RequestBody ImgInfoQo imgInfoQo) {
        Long id = pexelsImgBiz.saveImg(imgInfoQo);
        return Result.buildSuccessData(id);
    }

}