package cc.pptshow.build.pptbuilder.biz.builder.helper;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.biz.builder.ChannelBiz;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelHelper {

    @Autowired
    private List<ChannelBiz> channelBizList;

    public FilePPTInfo buildFileInfo(String pptPath, GlobalStyle globalStyle, Integer dataTitleId) {
        ChannelBiz channelBiz = channelBizList.stream()
                .filter(c -> c.myChannel().equals(globalStyle.getChannelType()))
                .findFirst().orElseThrow(RuntimeException::new);
        return channelBiz.channelDo(pptPath, globalStyle, dataTitleId);
    }

}
