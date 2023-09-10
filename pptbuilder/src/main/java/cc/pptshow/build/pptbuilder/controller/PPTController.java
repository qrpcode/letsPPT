package cc.pptshow.build.pptbuilder.controller;

import cc.pptshow.build.pptbuilder.biz.builder.PPTBuildBiz;
import cc.pptshow.build.pptbuilder.biz.builder.PPTRemakeBiz;
import cc.pptshow.build.pptbuilder.biz.helper.EsHelper;
import cc.pptshow.build.pptbuilder.domain.qo.ImgCallBack;
import cc.pptshow.build.pptbuilder.domain.vo.Result;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/ppt")
public class PPTController {

    @Resource
    private PPTBuildBiz pptBuildBiz;

    @Resource
    private EsHelper esHelper;

    @Resource
    private PPTRemakeBiz pptRemakeBiz;

    @PostMapping("/buildByData")
    public Result buildByData(@RequestParam("id") Long id) {
        long begin = System.currentTimeMillis();
        pptBuildBiz.buildByFileData(id);
        long end = System.currentTimeMillis();
        return Result.buildSuccessData("耗时：" + ((end - begin) / 1000.000) + "秒");
    }

    @PostMapping("/img/callBack")
    public String toImgCallBack(@RequestBody ImgCallBack imgCallBack) {
        System.out.println(JSON.toJSONString(imgCallBack));
        System.out.println(imgCallBack.getImgPath());
        System.out.println(imgCallBack.getPptPath());
        System.out.println(new File(imgCallBack.getImgPath()).exists());
        return "ok";
    }

    @GetMapping("/flushEs")
    public String flushPPTInEs() {
        esHelper.flushAllPPT();
        return "ok";
    }

    @GetMapping("/flushTagEs")
    public String flushTagInEs() throws InterruptedException {
        esHelper.flushAllTag();
        return "ok";
    }

    @GetMapping("/flushTagNlp")
    public String flushTagNlp() {
        pptRemakeBiz.flushTagNlp();
        return "ok";
    }

    @GetMapping("/deleteAndFlush")
    public String deleteAndFlush(@RequestParam("uid") String uid) {
        pptRemakeBiz.deleteAndFlush(uid);
        return "ok";
    }

    @PostMapping("/deleteAndFlushList")
    public String deleteAndFlush(@RequestBody List<String> uids) {
        for (String uid : uids) {
            try {
                pptRemakeBiz.deleteAndFlush(uid);
            } catch (Throwable t) {
                log.error("deleteAndFlushList 出现异常", t);
            }
        }
        return "ok";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("uid") String uid) {
        pptRemakeBiz.delete(uid);
        return "ok";
    }

    @GetMapping("/addHomeBlackList")
    public String addHomeBlackList(@RequestParam("uid") String uid) {
        pptRemakeBiz.addHomeBlackList(uid);
        return "ok";
    }

    @GetMapping("/addContentBlackList")
    public String addContentBlackList(@RequestParam("uid") String uid) {
        pptRemakeBiz.addContentBlackList(uid);
        return "ok";
    }

    @GetMapping("/addBigTitleBlackList")
    public String addBigTitleBlackList(@RequestParam("uid") String uid) {
        pptRemakeBiz.addBigTitleBlackList(uid);
        return "ok";
    }

    @GetMapping("/addBlackByUidAndPageNumber")
    public String addBlackByUidAndPageNumber(@RequestParam("uid") String uid,
                                             @RequestParam("pageNumber") String pageNumber) {
        pptRemakeBiz.addBlackByUidAndPageNumber(uid, pageNumber);
        return "ok";
    }

}
