package cc.pptshow.build.pptbuilder.biz.builder.impl.channel;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.biz.builder.ChannelBiz;
import cc.pptshow.build.pptbuilder.biz.builder.helper.FileInfoSetHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.ChannelConfig;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MbgChannelBiz implements ChannelBiz {

    @Resource
    private FileInfoSetHelper fileInfoSetHelper;

    @Override
    public ChannelType myChannel() {
        return ChannelType.MBG;
    }

    @Override
    public FilePPTInfo channelDo(String pptPath, GlobalStyle globalStyle, Integer dataTitleId) {
        ChannelConfig channelConfig = BConstant.config.get(ChannelType.MBG.getCode());
        return fileInfoSetHelper.buildMainField(pptPath, globalStyle, dataTitleId, channelConfig);
    }

}
