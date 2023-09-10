package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.biz.element.HomePicBiz;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/element")
public class ElementController {

    @Resource
    private HomePicBiz homePicBiz;

    @GetMapping("/save")
    public String saveElement(@RequestParam("uri") String uri,
                              @RequestParam("begin") Integer begin,
                              @RequestParam("end") Integer end) throws Exception {
        homePicBiz.saveElement(uri, begin, end);
        return "ok";
    }

}
