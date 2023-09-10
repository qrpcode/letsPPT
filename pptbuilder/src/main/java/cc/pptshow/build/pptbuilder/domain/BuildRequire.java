package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 构建需求
 */
@Data
public class BuildRequire {

    private Integer dataTitleId;

    private String title;

    private List<Integer> pageTypes = Lists.newArrayList();

    private Integer colorStyle;

    private Integer pptStyle;

    private ChannelType channelType;

}
