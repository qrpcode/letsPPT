package cc.pptshow.build.pptbuilder.biz.builder;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;

/**
 * 不同渠道需要做的事
 */
public interface ChannelBiz {

    ChannelType myChannel();

    FilePPTInfo channelDo(String pptPath, GlobalStyle globalStyle, Integer dataTitleId);

}
