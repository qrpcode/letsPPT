package cc.pptshow.build.pptbuilder.library;

import cc.pptshow.build.pptbuilder.bean.ConvertPptPng;
import cc.pptshow.build.pptbuilder.service.ConvertPPTPngService;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 通过 dll 调用系统文件
 * 需要保证系统 c盘 下存在 “pptBuilder” 文件夹，且存在需要的exe
 */
@Slf4j
@Service
public class OfficeLibrary {

    @Resource
    private ConvertPPTPngService convertPPTPngService;

    /**
     * 【同步】将ppt转换为jpg图片
     */
    @SneakyThrows
    public void syncPPT2Jpg(String pptPath, String pngPath, Object object) {
        //写入内容
        long id = insertConvertMsg(pptPath, pngPath, object);
        //等待完成
        int state = readConvertState(id);
        while (state == 0 || state == 1) {
            Thread.sleep(1000);
            state = readConvertState(id);
        }
        if (state != 2) {
            throw new RuntimeException("抱歉，转换失败了！");
        }
    }

    private int readConvertState(long id) {
        ConvertPptPng convertPPTPng = convertPPTPngService.selectById(id);
        return convertPPTPng.getState();
    }

    private long insertConvertMsg(String pptPath, String pngPath, Object object) {
        if (!pngPath.endsWith("\\") && !pngPath.endsWith("/")) {
            pngPath += "\\";
        }
        ConvertPptPng pptPng = new ConvertPptPng();
        pptPng.setFilePath(pptPath);
        pptPng.setImgPath(pngPath);
        pptPng.setState(0);
        pptPng.setObject(JSON.toJSONString(object));
        convertPPTPngService.insert(pptPng);
        return pptPng.getId();
    }

    /**
     * 【异步】将ppt转换为jpg图片
     */
    @SneakyThrows
    public void asyncPPT2Jpg(String pptPath, String pngPath, Object object) {
        insertConvertMsg(pptPath, pngPath, object);
    }


}
