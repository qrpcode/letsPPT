package cc.pptshow.build.pptbuilder.biz.builder.helper;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import static cc.pptshow.build.pptbuilder.constant.BConstant.SYS_PATH;

@Service
public class OfficeHelper {

    /**
     * 对PPT的语法进行纠正
     * 实际上就是office打开自动另存为一下
     *
     * @param pptPath 文件路径
     */
    @SneakyThrows
    public void correctPPTXml(String pptPath) {
        /*pptPath = pptPath.replace("\\", "/");
        String[] cmd = new String[]{"wscript", SYS_PATH + "pptclean.vbs", pptPath};
        Process process = Runtime.getRuntime().exec(cmd);*/
    }

}
