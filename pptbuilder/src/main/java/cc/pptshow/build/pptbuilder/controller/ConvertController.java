package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.bean.ConvertPptPng;
import cc.pptshow.build.pptbuilder.domain.vo.Result;
import cc.pptshow.build.pptbuilder.service.ConvertPPTPngService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 文件转换专用
 */
@RestController
@CrossOrigin
@RequestMapping("/dataFile")
public class ConvertController {

    @Resource
    private ConvertPPTPngService convertPptPngService;

    @GetMapping("/findLastNeedDo")
    public synchronized ConvertPptPng findLastNeedDo() {
        ConvertPptPng convertPptPng = convertPptPngService.signLastNotConvert();
        if (Objects.isNull(convertPptPng)) {
            return new ConvertPptPng();
        }
        return convertPptPng;
    }

    @GetMapping("/ppt2png/finish")
    public synchronized Result ppt2pngFinish(@RequestParam("id") Long id) {
        convertPptPngService.updateFinish(id);
        return Result.buildSuccessResult();
    }

}
