package cc.pptshow.build.pptbuilder.biz.builder.impl.channel;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.biz.builder.ChannelBiz;
import cc.pptshow.build.pptbuilder.biz.builder.helper.FileInfoSetHelper;
import cc.pptshow.build.pptbuilder.biz.builder.helper.OfficeHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.ChannelConfig;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

@Slf4j
@Service
public class GlobalChannelBiz implements ChannelBiz {

    @Resource
    private FileInfoSetHelper fileInfoSetHelper;

    @Resource
    private OfficeHelper officeHelper;

    @Override
    public ChannelType myChannel() {
        return ChannelType.GLOBAL;
    }

    @Override
    public FilePPTInfo channelDo(String pptPath, GlobalStyle globalStyle, Integer dataTitleId) {
        try {
            ChannelConfig channelConfig = BConstant.config.get(ChannelType.GLOBAL.getCode());
            FilePPTInfo filePPTInfo = fileInfoSetHelper.buildMainField(pptPath, globalStyle, dataTitleId, channelConfig);
            //String date = DateUtil.format(new Date(), "yyyyMMdd");
            //new File(channelConfig.getFilesPath() + date).mkdirs();
            //String newFileName = channelConfig.getFilesPath() + date + "\\" + globalStyle.getTitle() + ".pptx";
            //newFileName = newFileName.replace(" ", "");
            //FileUtil.copy(pptPath, newFileName, true);
            officeHelper.correctPPTXml(pptPath);
            return filePPTInfo;
        } catch (Throwable t) {
            log.error("[出错了] ", t);
            throw t;
        }
    }
}
