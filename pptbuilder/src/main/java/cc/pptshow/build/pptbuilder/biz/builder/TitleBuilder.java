package cc.pptshow.build.pptbuilder.biz.builder;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;

public interface TitleBuilder {

    FilePPTInfo buildByTitleId(Long id, ChannelType channelType);

    void buildAll();

    void buildByRemark();

    void buildTodayGlobal();

}
